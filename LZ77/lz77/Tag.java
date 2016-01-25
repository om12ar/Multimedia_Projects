
package lz77;

import java.io.Serializable;

public class Tag implements Serializable{
    byte index;
    byte length;
    char nextCharacter;

    public Tag() {
    }
    
    
    Tag(byte i , byte l , char n){
        index = i ;
        length = l ;
        nextCharacter = n ;
    }
    
    
@Override
public String toString() {
	return "<"+index+" "+length+" "+nextCharacter+">";
}
    
}
