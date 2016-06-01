/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package busqueda;

import edatos.*;
/**
 *
 * @author ealonso
 */
public class TNodoABH extends TNodoAB {
    public float heu;
    
    public TNodoABH( Object e, TArbol p, String a, int pr, float cost, float heu ) {
        super(e, p, a, pr,cost);
        this.heu = heu;
    }
    
}
