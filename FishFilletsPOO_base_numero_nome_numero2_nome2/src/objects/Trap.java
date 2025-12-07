package objects;

import java.util.ArrayList;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Trap extends MovableObject implements Dangerous, Transpassable {

	public Trap(Room room) {
		super(room, true, true, true);
	}

	@Override
	public String getName() { return "trap"; }

	@Override
	public int getLayer() { return 1; }

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof BigFish || character instanceof Crab;
	}

	@Override
	public boolean isPassableFor(GameObject obj) {
		return obj instanceof GameCharacter;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (isPassableFor(actor)) {
			Point2D actorsNextPosition = actor.getPosition().plus(dir.asVector());
			if (actorsNextPosition.equals(this.getPosition())) {
				if (isLethalTo(actor) || actor.isEnemy()) {
					if (actor.isEnemy()) {
						engine.getCurrentRoom().removeObject(actor);
					} else {
						String nomePeixe = (actor instanceof SmallFish) ? "Peixe Pequeno" : "Peixe Grande";
						actor.setFishDeath(true);
						engine.triggerGameOver("O " + nomePeixe + " morreu na armadilha!");
					}
					return true; 
				}
				return true; 
			}
		}
		return super.interact(actor, dir, engine);
	}
	@Override
    public void processGravity(GameEngine engine) {
        Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
        
        // Criamos uma cópia da lista (new ArrayList) porque podemos remover o Caranguejo dentro do loop
        for (GameObject obj : new ArrayList<>(engine.getCurrentRoom().getObjects())) {
            if (obj.getPosition().equals(posBelow)) {
                
                // LÓGICA NÃO HARDCODED:
                // Perguntamos: "Isto é uma personagem? E eu sou letal para ela?"
                // O método isLethalTo já diz que é letal para BigFish e Crab.
                if (obj instanceof GameCharacter && isLethalTo((GameCharacter) obj)) {
                    
                    GameCharacter victim = (GameCharacter) obj;

                    if (victim.isEnemy()) {
                        // Se for inimigo (Caranguejo), remove-o do jogo.
                        engine.getCurrentRoom().removeObject(victim);
                        
                        // NOTA: Não fazemos 'return'. Deixamos o código seguir para o 
                        // super.processGravity para que a armadilha caia imediatamente 
                        // para o espaço vazio deixado pelo caranguejo.
                    } else {
                        // Se não for inimigo (Peixe Grande), é Game Over.
                        String nomePeixe = (victim instanceof SmallFish) ? "Peixe Pequeno" : "Peixe Grande";
                        victim.setFishDeath(true);
                        engine.triggerGameOver("O " + nomePeixe + " foi esmagado pela armadilha!");
                        return; // O jogo acabou, paramos aqui.
                    }
                }
            }
        }
        
        // Executa a gravidade normal (cair se o espaço estiver vazio)
        super.processGravity(engine);
    }
}