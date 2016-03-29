package com.materlistview.event;


import com.materlistview.cardModel.Card;

public class DismissEvent {

    private final Card dismissedCard;

    public DismissEvent(Card dismissedCard) {
        this.dismissedCard = dismissedCard;
    }

    public Card getDismissedCard() {
        return dismissedCard;
    }
}
