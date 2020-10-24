/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import java.util.Vector;

/**
 *
 * @author phandungtri
 */
public class Room {
    Vector<Player> players;
    
    public Room() {
        players = new Vector();
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }
}
