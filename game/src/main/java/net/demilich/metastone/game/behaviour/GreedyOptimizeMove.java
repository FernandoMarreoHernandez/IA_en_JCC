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

public class GreedyOptimizeMove extends Behaviour {
	private final static Logger logger = LoggerFactory.getLogger(GreedyOptimizeMove.class);

	private final IGameStateHeuristic heuristic;

	public GreedyOptimizeMove(IGameStateHeuristic heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public String getName() {
		return "Sujeto";
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
		//AccionesTurno.setIdsCartasTurno(validActions); //metodo para el vector de los ids de las cartas usables en el turno
		if (validActions.size() == 1) {
			AccionesPartida.obtenerMejorValorColumna(AccionesTurno.matrizTurno, AccionesTurno.idCartasTurno);
			return validActions.get(0);
		}
		GameAction bestAction = validActions.get(0);
		double bestScore = -999.0;
		for (GameAction gameAction : validActions) {
			GameContext simulationResult = simulateAction(context.clone(), player, gameAction);
			double gameStateScore = heuristic.getScore(simulationResult, player.getId());
			AccionesTurno.guardarValorCarta(gameAction, gameStateScore, validActions);
			if (gameStateScore > bestScore) {
				bestScore = gameStateScore;
				bestAction = gameAction;
				logger.debug("BEST ACTION SO FAR id:{}", bestAction.hashCode());
			}
			simulationResult.dispose();

		}
		if (bestAction.toString().contains("SUMMON")) {
			AccionesTurno.CambiarIdCarta(bestAction, IDNuevoEsbirro.getIdNuevoEsbirro());
		}
		AccionesTurno.sumarNumeroAcciones(); //metodo para sumar 1 al numero de acciones para la siguiente acción

		//ENTRA EN EL IF SI LA MEJOR ACCION ES END_TURN
		if (bestAction.toString().contains("END_TURN")) {
			AccionesPartida.obtenerMejorValorColumna(AccionesTurno.matrizTurno, AccionesTurno.idCartasTurno);
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
