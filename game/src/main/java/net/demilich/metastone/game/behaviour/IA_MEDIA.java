package net.demilich.metastone.game.behaviour;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.logic.GameLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IA_MEDIA extends Behaviour {
    private final static Logger logger = LoggerFactory.getLogger(IA_MEDIA.class);

    private final IGameStateHeuristic heuristic;

    public IA_MEDIA(IGameStateHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "IA_MEDIA";
    }


    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        List<Card> discardedCards = new ArrayList<Card>();
        for (Card card : cards) {
            if (card.getBaseManaCost() >= 4) {
                discardedCards.add(card);
            }
        }
        return discardedCards;
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        AccionesTurnoMedia.setIdsCartasTurno(validActions); //metodo para el vector de los ids de las cartas usables en el turno

        if (validActions.size() == 1) {
            AccionesPartidaMedia.obtenerMejorValorColumna(AccionesTurnoMedia.matrizTurno, AccionesTurnoMedia.idCartasTurno);
            return validActions.get(0);
        }
        GameAction bestAction = validActions.get(0);
        double bestScore = -999.0;
        for (GameAction gameAction : validActions) {
            GameContext simulationResult = simulateAction(context.clone(), player, gameAction);
            int id = AccionesTurnoMedia.ObtenerIDCarta(gameAction);
            if (id != 67 && id != 36 && id != 4 && id != 0 && Objects.equals(gameAction.getActionType().toString(), "PHYSICAL_ATTACK")) {
                id = AccionesTurnoMedia.transformarId(id);
            }
            double gameStateScore = heuristic.getScoreId(simulationResult, player.getId(),id);
            AccionesTurnoMedia.guardarValorCarta(gameAction, gameStateScore, validActions);
            if (gameStateScore > bestScore) {
                bestScore = gameStateScore;
                bestAction = gameAction;
                logger.debug("BEST ACTION SO FAR id:{}", bestAction.hashCode());
            }
            simulationResult.dispose();

        }
        if (bestAction.toString().contains("SUMMON")) {
            AccionesTurnoMedia.CambiarIdCarta(bestAction, IDNuevoEsbirro.getIdNuevoEsbirro());
        }
        AccionesTurnoMedia.sumarNumeroAcciones(); //metodo para sumar 1 al numero de acciones para la siguiente acción

        //ENTRA EN EL IF SI LA MEJOR ACCION ES END_TURN
        if (bestAction.toString().contains("END_TURN")) {
            AccionesPartidaMedia.obtenerMejorValorColumna(AccionesTurnoMedia.matrizTurno, AccionesTurnoMedia.idCartasTurno);
            //AccionesPartida.mostrarTablaPartida();
        }
        return bestAction;
    }

    private GameContext simulateAction(GameContext simulation, Player player, GameAction action) {
        GameLogic.logger.debug("");
        GameLogic.logger.debug("********SIMULATION starts********** " + simulation.getLogic().hashCode());
        simulation.getLogic().performGameAction(player.getId(), action);
        GameLogic.logger.debug("********SIMULATION ends**********");
        GameLogic.logger.debug("");
        return simulation;
    }
}
