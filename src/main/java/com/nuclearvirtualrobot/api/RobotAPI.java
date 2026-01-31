package com.nuclearvirtualrobot.api;

import com.nuclearvirtualrobot.menus.RobotMenuExample;
import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.service.RobotService;
import com.nuclearvirtualrobot.store.RobotState;
import com.nuclearvirtualrobot.util.CustomHeadFactory;
import com.nuclearvirtualrobot.util.RobotHeadTextures;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RobotAPI {
    private final RobotService service;

    public RobotAPI(RobotService service) {
        this.service = service;
    }

    /**
     * Cria um ativador para o tipo/economia informados.
     * O item sempre vem com quantidade 1, contendo o valor total no metadata.
     */
    public ItemStack createActivator(RobotType type, RobotEconomy economy, int amount) {
        return service.getActivatorItem().createActivator(type, economy, amount);
    }

    /**
     * Cria apenas a head customizada do robô para uso em menus.
     */
    public ItemStack createHead(RobotEconomy economy) {
        return switch (economy) {
            case TOKENS -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_TOKENS);
            case TOXINA -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_TOXINA);
            case CASH -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_CASH);
        };
    }

    /**
     * Adiciona robôs diretamente ao jogador (sem item ativador).
     */
    public void giveRobots(Player player, RobotType type, RobotEconomy economy, int amount) {
        service.activateRobots(player, type, economy, amount);
    }

    /**
     * Retorna o estado atual do robô do jogador.
     */
    public RobotState getState(Player player, RobotType type, RobotEconomy economy) {
        return service.getState(player, type, economy);
    }

    /**
     * Abre o menu principal (/robo) para o jogador.
     */
    public void openMainMenu(Player player) {
        RobotMenuExample.openMainMenu(player);
    }
}
