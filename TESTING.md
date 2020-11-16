Testing is performed through two files:
	TestEntities.scala  (reader/src/test/scala/org/parsertongue/mr/logx/entities/TestEntities.scala) 
and TestEvents.scala (reader/src/test/scala/org/parsertongue/mr/logx/events/TestEvents.scala), 
that utilize the testing functions defined in TestUtils.scala (reader/src/test/scala/org/parsertongue/mr/TestUtils.scala).

Both TestEntities and TestEvents call the same boolean function, checkMention.
checkMention operates on two inputs: a sequence of Mentions (extracted by the MachineReadingSystem using the Odin rules in logx-reader), and a MentionTestCase.
MentionTestCase is split into two cases; ExistsMentionTestCase, and ForAllMentionTestCase. The names of the two cases indicate the quantifier applying to evaluation of the sequence of Mentions, i.e. find at least one Mention such that..., or insure that for all Mentions ... .
This different quantification is directly reflected in the evaluation procedure at the level of checkMention:
    ExistsMentionTestCase:	```mentions.exists { m => em.check(m)```
    ForAllMentionTestCase:	```mentions.forall { m => em.check(m)```

All functions called by checkMention have their own "check" boolean function.
MentionTestCase (either variety)'s check function calls the check functions of its TextTestCase and LabelTestCase(s) obligatorily, and calls check from ArgTestCase if present.
ArgTestCase, in turn, through its check function, evaluates the check functions of its own TextTestCase, LabelTestCase(s), and RoleTestCase(s).

The bottom-level functions TextTestCase, LabelTestCase, RoleTestCase each have two varieties indicated by prefix: Positive-/Negative-. As the names suggest, a PositiveTextTestCase returns true if its string argument matches the text value of the Mention submitted to its check function. A NegativeTextTestCase returns true if its string argument does NOT match the Mention's text field.

Entity and Event tests differ in that Entity tests do not involve any Arg or Role tests, only text and Label checks.

To illustrate the potential utility of negative testing: the entity rules include patterns for dates, such as "June 12, 2000". However, a possible confusion arises from the format of time-of-day expressions in military documents, where we might see "June 12, 2000 hours" (i.e., 8 P.M. on June 12). Of course, the entity rules themselves have a variety of means available to enforce this restriction: negative lookahead in the date rule, higher-priority military-time rules, etc. But to test proper functioning, we can write tests that might either seek to verify that at least one mention found in some piece of text has such-and-such properties, or deny that any mentions have some undesirable configuration of properties.

Complexities arise because MentionTestCases and ArgTestsCases contain multiple subtests (Text-, Label-, Role-). What combinations of polarities among subtests (e.g. all positive, all negative, a mixture of positive and negative) fall within the expected range of use?
Consider first the simplest case, corresponding to entity tests: MentionTests without Arg tests. 
mix: 
(Pos label, neg text),- make sure the given label is picked up but doesn't correspond to this string?; 
(neg label, pos text)<--make sure this string is not read as... 
Running with the above example, suppose the parent MentionTestCase has text "June 12, 2100 hours". The MentionTestCase can either be ExistsTestCase, or ForAllTestCase.

    Exists(PosLbl("date"), NegTxt("June12, 2100")). There's a date that is not the string __. ~
    Exists(NegLbl("date"), PosTxt("2100 hours")) <-- some Mention isn't a date but has text __~

    ForAll(PosLbl("date"), NegTxt("June12, 2100")): all mentions are dates without text __. ??
    ForAll(NegLbl("date"), PosTxt("2100 hours")) <--all mentions aren't dates but have text __??

In fact, combinations under ForAll seem of little use. For this reason, and to avoid possible confusion, we have written into the function definitions the requirement that ForAll scopes over all-negative tests. 
Combinations of positive and negative subtests under Exists are potentially useful. in keeping with the flexibility outlined for some Argtests below, we allow ExistsMentiontestCases to freely embed either polarity of label and text tests.

ArgTest polarity adds a further layer of potential complexity. To simplify use, we hold the local polarity of ArgTests themselves to the polarity convention: ForAll(NegLbl, NegTxt, NegArg(...)). Exists may embed PosArgTests and/or NegArgTests.
When it comes to polarity of Arg subtests, we keep NegArg to embed strictly positive Text-, Label-, and Role- subtests. Allowing NegArg tests to embed negative subtests, or a mix of positive and negative subtests, adds unwanted complexity unrewarded by clear use cases.

    ForAll(NegLbl, NegTxt, NegArg(PosLbl, PosRole, PosTxt))

To allow desired flexibility with easily-understood uses, we allow the subtests within Positive Arg tests (only) to be positive or negative, independently of other subtests within the same Argtest.

    Exists(PosArg(PosLbl, PosRole, NegText): this label and role are present, but not corresponding to this string.
    Exists(PosArg(PosLbl, NegRole, PosText): some Mention has an argument with this text and label, but not this role.
    Exists(PosArg(NegLbl, PosRole, PosText): some argument with this role and text, but not this label.
    etc.

