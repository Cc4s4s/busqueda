/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package busqueda;

import edatos.*;
/**
 *
 * @author laboratorio
 */
public class TNodoAB {
  public static int  nnodos = 0;
  public Object  estado;
  public TArbol  padre;
  public String  accion;
  public int     profundidad;
  public float   costo;

  // constructor
  public TNodoAB( Object e, TArbol p, String a, int pr, float cost )
  {
      estado = e;
      padre = p;
      accion = a;
      profundidad = pr;
      costo = cost;
      nnodos++;
  }
  
  public String toString()
  {
      if ( !padre.vacio()  )
         return " accion: " + accion + " >> " + estado;
      else
         return " inicio: " + estado.toString();
  }

}
