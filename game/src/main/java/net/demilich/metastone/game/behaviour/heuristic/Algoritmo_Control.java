package net.demilich.metastone.game.behaviour.heuristic;

import net.demilich.metastone.game.AdministradorJson;
import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.minions.Minion;

public class Algoritmo_Control implements IGameStateHeuristic {

    private double calculateFriendlyMinionScore(Minion minion) {
        double minionScore = minion.getAttack() + minion.getHp();
        double baseScore = minionScore;
        if (minion.hasAttribute(Attribute.FROZEN)) {
            return minion.getHp();
        }
        if (minion.hasAttribute(Attribute.TAUNT)) {
            minionScore *= 2;
        }
        if (minion.hasAttribute(Attribute.WINDFURY)) {
            minionScore += minion.getAttack();
        }
        if (minion.hasAttribute(Attribute.DIVINE_SHIELD)) {
            minionScore += minion.getAttack() * 0.5f;
        }
        if (minion.hasAttribute(Attribute.SPELL_DAMAGE)) {
            minionScore += 1;
        }
        if (minion.hasAttribute(Attribute.ENRAGED)) {
            minionScore += 1.5f *  minion.getAttack();
        }
        if (minion.hasAttribute(Attribute.STEALTH)) {
            minionScore += 1;
        }
        if (minion.hasAttribute(Attribute.UNTARGETABLE_BY_SPELLS)) {
            minionScore += 1;
        }
        return minionScore;
    }
    private float calculateEnemyMinionScore(Minion minion) {
        float minionScore = minion.getAttack() + minion.getHp();
        float baseScore = minionScore;
        if (minion.hasAttribute(Attribute.FROZEN)) {
            return minion.getHp();
        }
        if (minion.hasAttribute(Attribute.TAUNT)) {
            minionScore += 2;
        }
        if (minion.hasAttribute(Attribute.WINDFURY)) {
            minionScore += minion.getAttack() * 2;
        }
        if (minion.hasAttribute(Attribute.DIVINE_SHIELD)) {
            minionScore += minion.getAttack() * 1.5f;
        }
        if (minion.hasAttribute(Attribute.SPELL_DAMAGE)) {
            minionScore += 1;
        }
        if (minion.hasAttribute(Attribute.ENRAGED)) {
            minionScore += 1;
        }
        if (minion.hasAttribute(Attribute.STEALTH)) {
            minionScore += 1;
        }
        if (minion.hasAttribute(Attribute.UNTARGETABLE_BY_SPELLS)) {
            minionScore += minion.getAttack() * 0.5f;
        }
        return minionScore;
    }

    @Override
    public double getScore(GameContext context, int playerId) {
        return 0;
    }

    @Override
    public double getScoreId(GameContext context, int playerId, int cardId) {
        double score = 0;
        if (cardId < 35 && cardId != 4 && cardId != 0) {
           score = score + (AdministradorJson.ObtenerUnValor(cardId) * 0.2);
        }
        Player player = context.getPlayer(playerId);
        Player opponent = context.getOpponent(player);
        if (player.getHero().isDestroyed()) {
            return Float.NEGATIVE_INFINITY;
        }
        if (opponent.getHero().isDestroyed()) {
            return Float.POSITIVE_INFINITY;
        }
        int ownHp = player.getHero().getHp() + player.getHero().getArmor();
        int opponentHp = opponent.getHero().getHp() + opponent.getHero().getArmor();
        score += ownHp - opponentHp;
        score += player.getHand().getCount() * 2;
        score -= opponent.getHand().getCount() * 2;
        score += player.getMinions().size() * 3;
        score -= opponent.getMinions().size() * 3.5f;
        for (Minion minion : player.getMinions()) {
            score += calculateFriendlyMinionScore(minion);
        }
        for (Minion minion : opponent.getMinions()) {
            score -= calculateEnemyMinionScore(minion);
        }

        return score;
    }

    @Override
    public void onActionSelected(GameContext context, int playerId) {
    }

}
