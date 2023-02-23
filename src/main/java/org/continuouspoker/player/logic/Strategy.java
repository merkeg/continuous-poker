package org.continuouspoker.player.logic;

import org.continuouspoker.player.model.*;

import java.util.List;

public class Strategy {

   private static final String TEAMNAME = "offsuit";

   public Bet decide(final Table table) {
      System.out.println(table);
      Player p = table.getPlayers().get(table.getActivePlayer());
      List<Card> cards = p.getCards();
      int totalWorth = getTotalWorth(cards);

      if(getTotalWorth(cards) >= 24) { // größer als 20

         if(hasPair(cards)) {
            return new Bet().bet(p.getStack());
         }

         return new Bet().bet(p.getStack());
      }

      return new Bet().bet(0);
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

   public boolean hasPair(List<Card> cards) {
      int[] worths = getRanks(cards);
      return worths[0] == worths[1];
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
