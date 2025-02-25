package org.continuouspoker.player.logic;

import org.continuouspoker.player.model.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Strategy {

   private static final String TEAMNAME = "offsuit";

   public Bet decide(final Table table) {
      System.out.println(table);
      Player p = table.getPlayers().get(table.getActivePlayer());
      List<Card> cards = p.getCards();
      int totalWorth = getTotalWorth(cards);
      List<Player> otherPlayers = getOtherPlayers(table.getPlayers());

      if(p.getStack() >= 350 && totalWorth >= 18) {
         return new Bet().bet(p.getStack());
      }

      if(table.getRound() == 0) {
         if(getTotalWorth(cards) < 15) { // Wenn kleiner als 10, folden
            return new Bet().bet(0);
         }


         if(getPairs(cards) >= 1) { // Wenn Paar, all In
            return new Bet().bet(p.getStack());
         }

         if(totalWorth >= 20 && hasSameSuit(cards)) { // Wenn selber suit und wert >= 20
            return new Bet().bet(p.getStack());
         }

         if(p.getStack() <= 20) { // Chips weniger als 20
            return new Bet().bet(p.getStack());
         }

         if(totalWorth >= 26) { // Total worth >= 26
            return new Bet().bet(p.getStack());
         }
         return new Bet().bet(table.getMinimumBet() * 2);
      }

      List<Card> deckWithCommunity = joinPairs(cards, table.getCommunityCards());
      int pairsTotal = getPairs(deckWithCommunity);


      if(table.getRound() == 1) {
         if(hasTriplets(deckWithCommunity)) {
            return new Bet().bet(p.getStack());
         }

         if(hasOtherPlayerBetMore(table.getMinimumBet(), otherPlayers)) { // Wenn gegner höher geht
            if(pairsTotal == 0) {
               return new Bet().bet(0);
            } else if(pairsTotal == 1) {
               return new Bet().bet(table.getMinimumBet());
            }
            return new Bet().bet(p.getStack());
         }

         if(pairsTotal == 1) { // 1 pair mit community
            return new Bet().bet(table.getMinimumBet());
         } if(pairsTotal > 1) { // mehrere Pairs mit community
            return new Bet().bet(p.getStack());
         }
         return new Bet().bet(table.getMinimumBet());
      }





      return new Bet().bet(p.getStack());

   }



   public int getTotalWorth(List<Card> cards) {
      int sum = 0;
      for(int i : getRanks(cards)) {
         sum += i;
      }
      return sum;
   }

   public int[] getRanks(List<Card> cards) {
      int[] arr = new int[cards.size()];
      for(int i = 0; i < cards.size(); i++) {
         Card c = cards.get(i);
         arr[i] = getNumberizedRank(c.getRank());
      }

      return arr;
   }

   public boolean hasSameSuit(List<Card> cards) {
      return cards.get(0).getSuit() == cards.get(1).getSuit();
   }

   public int getPairs(List<Card> cards) {
      int pairs = 0;
      int[] worths = getRanks(cards);
      for(int i = 0; i < 2; i++) {
         for(int j = i+1; j < cards.size(); j++) {
            if(worths[i] == worths[j]) {
               pairs++;
               break;
            }
         }
      }
      return pairs;
   }

   public boolean hasTriplets(List<Card> cards) {
      int[] worths = getRanks(cards);
      int sameAmount = 1;
      for(int j = 1; j < worths.length; j++) {
         if(worths[0] == worths[j]) {
            sameAmount++;
         }
      }
      return sameAmount >= 3;
   }


   public List<Card> joinPairs(List<Card> cardsA, List<Card> cardsB) {
      List<Card> cards = Stream.concat(cardsA.stream(), cardsB.stream()).collect(Collectors.toList());
      return cards;
   }

   public List<Player> getOtherPlayers(List<Player> players) {
      return players.stream().filter( p -> !p.getName().equals(TEAMNAME)).collect(Collectors.toList());
   }

   public boolean hasOtherPlayerBetMore(int ourBet, List<Player> players) {
      for(Player other: players) {
         if(other.getBet() > ourBet){
            return true;
         }
      }
      return false;
   }

   public int getNumberizedRank(Rank r) {
      switch (r) {
         case J:
            return 11;
         case Q:
            return 12;
         case K:
            return 13;
         case A:
            return 14;
         default:
            return Integer.parseInt(r.toString());
      }
   }

}
