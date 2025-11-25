package objects;

public interface PushAgent {
	int getPushLimit(); 
	boolean canPushHeavy();  
	
	int getSupportLimit();  
	boolean canSupportHeavy(); 
}