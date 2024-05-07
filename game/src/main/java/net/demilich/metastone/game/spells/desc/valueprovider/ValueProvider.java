package net.demilich.metastone.game.spells.desc.valueprovider;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.TargetPlayer;

public abstract class ValueProvider {

	protected final ValueProviderDesc desc;

	public ValueProvider(ValueProviderDesc desc) {
		this.desc = desc;
	}

	public int getValue(GameContext context, Player player, Entity target, Entity host) {
		// Obtiene el tipo de jugador al que se aplica el valor del proveedor.
		TargetPlayer targetPlayer = (TargetPlayer) desc.get(ValueProviderArg.TARGET_PLAYER);

		// Si el tipo de jugador es nulo, establece el valor predeterminado como SELF.
		if (targetPlayer == null) {
			targetPlayer = TargetPlayer.SELF;
		}

		// Inicializa la variable para almacenar el jugador que proporciona el valor.
		Player providingPlayer = null;

		// Determina el jugador que proporciona el valor según el tipo de jugador especificado.
		switch (targetPlayer) {
			case ACTIVE:
				providingPlayer = context.getActivePlayer();
				break;
			case BOTH:
				// Si el tipo de jugador es BOTH, calcula el valor para ambos jugadores.
				int multiplier = desc.contains(ValueProviderArg.MULTIPLIER) ? desc.getInt(ValueProviderArg.MULTIPLIER) : 1;
				int offset = desc.contains(ValueProviderArg.OFFSET) ? desc.getInt(ValueProviderArg.OFFSET) : 0;
				int value = 0;

				// Itera sobre todos los jugadores en el contexto del juego.
				for (Player selectedPlayer : context.getPlayers()) {
					// Suma los valores proporcionados por el método abstracto provideValue.
					value += provideValue(context, selectedPlayer, target, host);
				}

				// Aplica el multiplicador y el offset al valor total.
				value = value * multiplier + offset;
				return value;
			case INACTIVE:
				providingPlayer = context.getOpponent(context.getActivePlayer());
				break;
			case OPPONENT:
				providingPlayer = context.getOpponent(player);
				break;
			case OWNER:
				providingPlayer = context.getPlayer(host.getOwner());
				break;
			case SELF:
			default:
				providingPlayer = player;
				break;
		}

		// Obtiene el multiplicador y el offset del proveedor, si están presentes.
		int multiplier = desc.contains(ValueProviderArg.MULTIPLIER) ? desc.getInt(ValueProviderArg.MULTIPLIER) : 1;
		int offset = desc.contains(ValueProviderArg.OFFSET) ? desc.getInt(ValueProviderArg.OFFSET) : 0;

		// Calcula el valor final aplicando el método abstracto provideValue y ajustando el multiplicador y el offset.
		int value = provideValue(context, providingPlayer, target, host) * multiplier + offset;
		return value;
	}

	// Método abstracto que debe ser implementado por las clases que heredan de ValueProvider.
	protected abstract int provideValue(GameContext context, Player player, Entity target, Entity host);
}
