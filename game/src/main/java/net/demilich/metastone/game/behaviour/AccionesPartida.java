package net.demilich.metastone.game.behaviour;

import java.util.ArrayList;

public class AccionesPartida {
    public static ArrayList<Integer> idCartasPartida = new ArrayList<Integer>(){{
        add(5);
        add(6);
        add(7);
        add(8);
        add(9);
        add(10);
        add(11);
        add(12);
        add(13);
        add(14);
        add(15);
        add(16);
        add(17);
        add(18);
        add(19);
        add(20);
        add(21);
        add(22);
        add(23);
        add(24);
        add(25);
        add(26);
        add(27);
        add(28);
        add(29);
        add(30);
        add(31);
        add(32);
        add(33);
        add(34);
    }};
    //con esto sigue los esbirros que se han jugado cuando cambian sus ids
    public static int[][] SeguimientoEsbirros = new int[2][60];
    public static int Turnos = 0;
    public static Double[][] matrizPartida = new Double[100][30];

    //metodo para obtener el mejor valor de cada columna de una tabla, ese valor se guardara en la tabla matriz partida
    public static void obtenerMejorValorColumna(Double[][] matrizTurno, ArrayList<Integer> idCartasTurno ) {
        //ESTO ESTA ENTRANDO Y GUARDANDO -9999 EN ALGUNOS CASOS
        //recorremos la matriz de turno columna por columna
        for (int i = 0; i < idCartasTurno.size(); i++) {
            double mejorValor = -9999;
            for (int j = 0; j < matrizTurno[i].length; j++) {
                //si la matriz de turno en la columna i y fila j no es nula
                if (matrizTurno[j][i] != null) {
                    //si el valor de la matriz de turno en la columna i y fila j es mayor al mejor valor
                    if (matrizTurno[j][i] > mejorValor &&
                        matrizTurno[j][i] != Float.NEGATIVE_INFINITY &&
                        matrizTurno[j][i] != Float.POSITIVE_INFINITY ){
                        //el mejor valor sera el valor de la matriz de turno en la columna i y fila j
                        mejorValor = matrizTurno[j][i].intValue();

                    }
                }
            }
            //buscamos el id de la carta
            int id = transformarId(idCartasTurno, i);
            //buscamos el id idCartasPartida y guardamos la posicion en la que se encuentra
            for (int k = 0; k < idCartasPartida.size(); k++) {
                if (id == idCartasPartida.get(k)) {
                    matrizPartida[Turnos][k] = mejorValor;
                }
            }

        }
        Turnos++;
    }

    private static int transformarId(ArrayList<Integer> idCartasTurno, int j) {
        int id = idCartasTurno.get(j);
        //si el valor es mayor o igual a 68 buscamos ese id en SeguimientoEsbirros
        if (idCartasTurno.get(j) >= 68) {
            for (int k = 0; k < 20; k++) {
                //si el id de la carta en la posicion j de idCartasPartida es igual al id de la carta en la posicion k de SeguimientoEsbirros
                if (idCartasTurno.get(j) == SeguimientoEsbirros[1][k]) {
                    //si el valor de la matriz de turno en la columna i y fila j es igual al mejor valor
                    id = SeguimientoEsbirros[0][k];
                }
            }
        }
        //si el id de la carta esta entre el 37 y el 66, el id se resta 32
        if (id >= 37 && id <= 66) {
            id = id - 32;
        }
        return id;
    }

    //metodo para mostrar la tabla de la partida
    public static void mostrarTablaPartida(){
        //muestra primero el vector de los ids de las cartas
        for (int i = 0; i < idCartasPartida.size(); i++) {
            System.out.print(idCartasPartida.get(i) + " ");
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 30; j++) {
                if (matrizPartida[i][j] != null){
                    System.out.print(matrizPartida[i][j] + " ");
                }
                else {
                    System.out.print("N ");
                }
            }
            System.out.println();
        }
    }
}
