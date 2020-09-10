package org.parsertongue.mr.logx.odin

import org.clulab.odin._
import org.parsertongue.mr.actions.OdinActions
import org.parsertongue.mr.logx.odin._

class LogxActions extends OdinActions {

def handleTransportEvent(mentions: Seq[Mention], state: State): Seq[Mention] = {
    mentions map {
        case transport if transport matches "Transport" => 
            // ensure mentions for `shipment` role have the label `Cargo`
            // (i.e., "Promote" shipment's label to `Cargo`)
            // TODO: extend Odin query language with `:OldLabel^NewLabel` syntax for label promotion
            val cargoRole = "shipment"
            val cargoMentions = transport.arguments.getOrElse(cargoRole, Nil)
            val newArgs = transport.arguments + (cargoRole -> cargoMentions.map(mkCargoMention))
            transport match {
                // invoke copy constructor for Mention subtypes w/ args
                case em: EventMention => em.copy(arguments = newArgs)
                case rel: RelationMention => rel.copy(arguments = newArgs)
                case cm: CrossSentenceMention => cm.copy(arguments = newArgs)
                case m => m
            }

        case other => other
    }
}

def mkCargoMention(m: Mention): Mention = m match {
    case cargo if cargo matches "Cargo" => m
    case nonCargo => nonCargo match {
        case tb: TextBoundMention     => tb.copy(labels = CARGO_LABELS)
        // NOTE: these shouldn't be necessary
        case em: EventMention         => em.copy(labels = CARGO_LABELS)
        case rel: RelationMention     => rel.copy(labels = CARGO_LABELS)
        case cm: CrossSentenceMention => cm.copy(labels = CARGO_LABELS)
    }

}
    val CARGO_LABELS = taxonomy.hypernymsFor("Cargo")
}