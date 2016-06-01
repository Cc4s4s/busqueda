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
public class TBusqueda {
  public TLista estados;
  public TArbol arbol;
  public TProblemaBusqueda pbc;

  // constructor
  public TBusqueda( TProblemaBusqueda pbc )
  {
      this.pbc = pbc;
      reiniciar();
  }

  public void reiniciar()
  {
      estados = new TListaA();
      arbol = null;
      TNodoAB.nnodos = 0;
  }
  
  private Object adicionarEstado( Object e  )
  {
      int i;
      i = estados.buscar(e);
      if ( i < 0 ) {
          estados.adicionar(e);
          i = estados.cantidad()-1;
      }
      return estados.obtener(i);
  }

  private boolean esobjetivo( Object e )
  {
      return pbc.esObjetivo(e);
  }
  
  private TNodoAB[] crearsolucion(TArbol araiz)
  {
      TNodoAB nodo;
      TNodoAB sol[];
      int t;
      nodo = (TNodoAB)araiz.obtenerDato();
      t = nodo.profundidad;
      sol = new TNodoAB[t+1];
      while ( t >= 0 && !araiz.vacio()  )
      {
          nodo = (TNodoAB)araiz.obtenerDato();
          sol[t--] = nodo;
          araiz = nodo.padre;
      }
      return sol;
  }

  private boolean buscarencamino(TArbol araiz, Object e)
  {
      while ( !araiz.vacio() )
      {
          TNodoAB nodo = (TNodoAB)araiz.obtenerDato();
          if ( nodo.estado.equals(e) )
              return true;
          araiz = nodo.padre;
      }
      return false;
  }

  private Object realizaraccion( Object e, int a )
  {
      Object ne;
      ne = pbc.verticeLlegada(e, a);
      if ( ne != null )
         return adicionarEstado( ne );
      else
         return null;
  }
  
  //primero en anchura
  public TNodoAB[] buscarSolucionPA( Object e )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TCola frontera;

      adicionarEstado( e );
      
      raiz = new TNodoAB( e, new TArbolA(), "", 0, 0 );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TColaP();
      frontera.insertar(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbolA)frontera.eliminar();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandir(araiz);
          while ( !exp.vacia() )
          {
              TArbol aux = (TArbol)(exp.obtener(0));
              if ( esobjetivo( ((TNodoAB)aux.obtenerDato()).estado ) )
                 return crearsolucion(aux);
              frontera.insertar(aux);
              exp.eliminar(0);
          }
      }
  }

  //primero en profundidad
  public TNodoAB[] buscarSolucionPP( Object e  )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TPila frontera;

      adicionarEstado( e );
      
      raiz = new TNodoAB( e, new TArbolA(), "", 0, 0 );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TPilaP();
      frontera.push(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbolA)frontera.pop();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandir(araiz);
          int c = exp.cantidad();
          while ( c > 0 )
          {
              TArbol aux = (TArbol)(exp.obtener(c-1));
              if ( esobjetivo( ((TNodoAB)aux.obtenerDato()).estado ) )
                 return crearsolucion(aux);
              frontera.push(aux);
              exp.eliminar(c-1);
              c--;
          }
      }
  }

  //costo uniforme
  public TNodoAB[] buscarSolucionCU( Object e  )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TColaPrior frontera;

      adicionarEstado( e );
      
      raiz = new TNodoAB( e, new TArbolA(), "", 0, 0 );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TColaPriorA(new TBusqueda.cmpCostoU(), false );
      frontera.insertar(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbol)frontera.eliminar();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandir(araiz);
          while ( !exp.vacia() )
          {
              TArbol aux = (TArbol)(exp.obtener(0));
              frontera.insertar(aux);
              exp.eliminar(0);
          }
      }
  }

  //heuristica: voraz
  public TNodoAB[] buscarSolucionHV( Object e  )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TColaPrior frontera;

      adicionarEstado( e );
      
      raiz = new TNodoABH( e, new TArbolA(), "", 0, 0, 
             pbc.heuristica(e) );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TColaPriorA(new TBusqueda.cmpCostoAV(), false );
      frontera.insertar(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbol)frontera.eliminar();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandirH(araiz);
          while ( !exp.vacia() )
          {
              TArbol aux = (TArbol)(exp.obtener(0));
              frontera.insertar(aux);
              exp.eliminar(0);
          }
      }
  }

  //heuristica: A*
  public TNodoAB[] buscarSolucionHA( Object e  )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TColaPrior frontera;

      adicionarEstado( e );
      
      raiz = new TNodoABH( e, new TArbolA(), "", 0, 0, 
             pbc.heuristica(e) );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TColaPriorA(new TBusqueda.cmpCostoAA(), false );
      frontera.insertar(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbol)frontera.eliminar();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandirH(araiz);
          while ( !exp.vacia() )
          {
              TArbol aux = (TArbol)(exp.obtener(0));
              frontera.insertar(aux);
              exp.eliminar(0);
          }
      }
  }

  //heuristica: A*, costo 1
  public TNodoAB[] buscarSolucionHA1( Object e  )
  {
      TNodoAB raiz,nodo;
      TArbol  araiz;
      TColaPrior frontera;

      adicionarEstado( e );
      
      raiz = new TNodoABH( e, new TArbolA(), "", 0, 0, 
             pbc.heuristica(e) );
      arbol = new TArbolA();
      arbol.crearHoja();
      arbol.modificarDato(raiz);
      frontera = new TColaPriorA(new TBusqueda.cmpCostoAA(), false );
      frontera.insertar(arbol);
      while ( true )
      {
          if ( frontera.vacia() )
              return null;
          araiz = (TArbol)frontera.eliminar();
          nodo = (TNodoAB)araiz.obtenerDato();
          if ( esobjetivo(nodo.estado) )
              return crearsolucion(araiz);
          TLista exp = expandirH1(araiz);
          while ( !exp.vacia() )
          {
              TArbol aux = (TArbol)(exp.obtener(0));
              frontera.insertar(aux);
              exp.eliminar(0);
          }
      }
  }


  //expandir para:
  // PP, PA y CU
  private TLista expandir(TArbol araiz)
  {
      TLista res;
      TNodoAB nodo;
      int i, NA;

      res = new TListaP();

      nodo = (TNodoAB)araiz.obtenerDato();
      NA = pbc.numeroArcosSalida(nodo.estado);
      for( i = 0; i < NA; i++ )
      {
              Object e = realizaraccion( nodo.estado, i );
              if ( e != null && !buscarencamino(araiz,e) )
              {
                TNodoAB n;
                n = new TNodoAB( e, araiz, String.valueOf(i) ,
                      nodo.profundidad+1,
                      nodo.costo + pbc.costo(nodo.estado, i)
                        );
                TArbol hijo = new TArbolA();
                hijo.crearHoja();
                hijo.modificarDato(n);
                araiz.adicionarHijo(hijo);
                res.adicionar(hijo);
              }
          }
      return res;
  }

  //expandir para:
  // HV y HA
  private TLista expandirH(TArbol araiz)
  {
      TLista res;
      TNodoAB nodo;
      int i, NA;

      res = new TListaP();

      nodo = (TNodoAB)araiz.obtenerDato();
      NA = pbc.numeroArcosSalida(nodo.estado);
      for( i = 0; i < NA; i++ )
      {
              Object e = realizaraccion( nodo.estado, i );
              if ( e != null && !buscarencamino(araiz,e) )
              {
                TNodoAB n;
                n = new TNodoABH( e, araiz, String.valueOf(i) ,
                      nodo.profundidad+1,
                      nodo.costo + pbc.costo(nodo.estado, i),
                      pbc.heuristica(e)
                        );
                TArbol hijo = new TArbolA();
                hijo.crearHoja();
                hijo.modificarDato(n);
                araiz.adicionarHijo(hijo);
                res.adicionar(hijo);
              }
          }
      return res;
  }

  //expandir para:
  // HA1
  private TLista expandirH1(TArbol araiz)
  {
      TLista res;
      TNodoAB nodo;
      int i, NA;

      res = new TListaP();

      nodo = (TNodoAB)araiz.obtenerDato();
      NA = pbc.numeroArcosSalida(nodo.estado);
      for( i = 0; i < NA; i++ )
      {
              Object e = pbc.verticeLlegada(  nodo.estado, i );
              if ( e != null && estados.buscar(e) < 0 )
              {
                adicionarEstado(e);
                TNodoAB n;
                n = new TNodoABH( e, araiz, String.valueOf(i) ,
                      nodo.profundidad+1,
                      nodo.costo + 1,
                      pbc.heuristica(e)
                        );
                TArbol hijo = new TArbolA();
                hijo.crearHoja();
                hijo.modificarDato(n);
                araiz.adicionarHijo(hijo);
                res.adicionar(hijo);
              }
          }
      return res;
  }
  
  private static class cmpCostoU implements TComparar {
      public int compara( Object o1, Object o2 ) {
          TNodoAB n1, n2;
          n1 = (TNodoAB)((TArbol)o1).obtenerDato();
          n2 = (TNodoAB)((TArbol)o2).obtenerDato();
          if ( n1.costo == n2.costo )
              return 0;
          else
              if ( n1.costo < n2.costo )
                  return -1;
              else
                  return 1;
      }
  }
  
  private static class cmpCostoAV implements TComparar {
      public int compara( Object o1, Object o2 ) {
          TNodoABH n1, n2;
          n1 = (TNodoABH)((TArbol)o1).obtenerDato();
          n2 = (TNodoABH)((TArbol)o2).obtenerDato();
          if ( n1.heu == n2.heu )
              return 0;
          else
              if ( n1.heu < n2.heu )
                  return -1;
              else
                  return 1;
      }
  }
      
  private static class cmpCostoAA implements TComparar {
      public int compara( Object o1, Object o2 ) {
          TNodoABH n1, n2;
          n1 = (TNodoABH)((TArbol)o1).obtenerDato();
          n2 = (TNodoABH)((TArbol)o2).obtenerDato();
          if ( n1.costo+n1.heu == n2.costo+n2.heu )
              return 0;
          else
              if ( n1.costo+n1.heu < n2.costo+n2.heu )
                  return -1;
              else
                  return 1;
      }
      
  }
  
}
  
