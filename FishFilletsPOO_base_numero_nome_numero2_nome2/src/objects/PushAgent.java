package objects;

import pt.iscte.poo.utils.Direction;

public interface PushAgent {
	int getPushLimit(Direction dir); 
	
	boolean canPushHeavy();  
	
	int getSupportLimit();  
	boolean canSupportHeavy(); 
}