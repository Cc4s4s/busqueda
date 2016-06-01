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
public abstract class TProblemaBusqueda implements TGrafoDin {
    
    public abstract boolean  esObjetivo( Object e );
    
    
    public float costo( Object vs, int a ) {
        return 0;
    }
    public float heuristica( Object v1 ) {
        return 0;
    }

    @Override
    public Object etiqueta( Object vs, int a ) {
        return null;
    }    
    
}
