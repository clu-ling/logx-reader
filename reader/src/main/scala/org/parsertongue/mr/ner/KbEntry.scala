package org.parsertongue.mr.ner

/**
  * Representation of a knowledge base and its corresponding taxonomic label.
  * @param kbName Unique name of a knowledge base file (minus its file extension)
  * @param neLabel The taxonomic label this knowledge base file should be added to
  */
case class KbEntry(kbName: String, neLabel: String)
