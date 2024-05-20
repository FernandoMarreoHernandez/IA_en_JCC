package net.demilich.metastone.game.behaviour;

import net.demilich.metastone.game.actions.GameAction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccionesTurnoMedia {
    public static ArrayList<Integer> idCartasTurno = new ArrayList<Integer>();
    public static Double[][] tablaTurno = new Double[20][20];
    public static int NumeroAcciones = 0;
    public static boolean nuevoObjetivo = false;


    //metodo para sumar 1 al numero de acciones
    public static void sumarNumeroAcciones(){
        NumeroAcciones++;
    }

    //metodo para el vector de los ids de las cartas usables en el turno, usa como parametro la lista de acciones validas
    //busca tanto por "cardId:(\\d+)]" como por "Attacker: [EntityReference id:(\\d+)]"
    public static void setIdsCartasTurno(List<GameAction> validActions) {

        for (GameAction gameAction : validActions) {
            int cardId = ObtenerIDCarta(gameAction);
            if (!idCartasTurno.contains(cardId) && cardId != 0 && cardId != 4 && cardId != 36 && cardId != 67) {
                idCartasTurno.add(cardId);
            }

        }
        if (idCartasTurno.isEmpty() && validActions.size() > 2){
            System.out.println("ERROR");
        }
    }

    //metodo para vaciar el vector de ids de las cartas del turno
    public static void vaciarIdsNumeroAccionesYTabla(){
        idCartasTurno.clear();
        NumeroAcciones = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                tablaTurno[i][j] = null;
            }
        }
    }

    //metodo para vaciar el SeguimientoEsbirros
    public static void vaciarSeguimientoEsbirros(){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 20; j++) {
                AccionesPartidaMedia.SeguimientoEsbirros[i][j] = 0;
            }
        }
    }

    public static void imprimirIdsCartasTurno(){
        System.out.println("Las cartas del turno son: ");
        for (int i = 0; i < idCartasTurno.size(); i++) {
            System.out.println(idCartasTurno.get(i));
        }
    }
    public static void guardarValorCarta(GameAction gameAction, double valor, List<GameAction> validActions){
        int cardId = 0;
        Pattern pattern = Pattern.compile("cardId:(\\d+)]");
        Pattern pattern1 = Pattern.compile("Attacker: \\[EntityReference id:(\\d+)]");
        Matcher matcher = pattern.matcher(gameAction.toString());
        Matcher matcher1 = pattern1.matcher(gameAction.toString());


        if (matcher.find()){
            String cardIdStr = matcher.group(1);
            cardId = Integer.parseInt(cardIdStr);
            nuevoObjetivo = true;
        }
        else if (matcher1.find()) {
            String cardIdStr = matcher1.group(1);
            cardId = Integer.parseInt(cardIdStr);
            nuevoObjetivo = true;
        }
        if (cardId != 4 && cardId != 36 && cardId != 67 && cardId != 0 ) {  //si es un poder de heroe o la moneda no se guarda
            idCartasTurno = idCartasTurno;
            int posicion = idCartasTurno.indexOf(cardId);
            if (posicion != -1) {
                if (nuevoObjetivo) {
                    nuevoObjetivo = false;
                    if (tablaTurno[NumeroAcciones][posicion] != null) {
                        if (valor > tablaTurno[NumeroAcciones][posicion]) {
                            tablaTurno[NumeroAcciones][posicion] = valor;
                        }
                    } else {
                        tablaTurno[NumeroAcciones][posicion] = valor;
                    }
                } else {
                    tablaTurno[NumeroAcciones][posicion] = valor;
                }
            }
            else{
                System.out.println("ESTO NO DEBERIA DE PASAR");
            }
        }
    }

    //metodo que imprime la matriz de turnos
    public static void imprimirtablaTurno(){
        System.out.println("La matriz de turnos es: ");
        //muestra primero el vector de ids
        for (int j = 0; j < idCartasTurno.size(); j++) {
            System.out.print(idCartasTurno.get(j) + " ");
        }
        System.out.println(" ");
        System.out.println("valores: ");
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(tablaTurno[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void CambiarIdCarta(GameAction idViejo, int idNuevo){
        //imprime el seguimiento de los esbirros
        Pattern pattern = Pattern.compile("cardId:(\\d+)]");
        Matcher matcher = pattern.matcher(idViejo.toString());;
        int ubicacion = 0;
        if (matcher.find()){
            String cardIdStr = matcher.group(1);
            int cardId = Integer.parseInt(cardIdStr);
            int posicion = idCartasTurno.indexOf(cardId);
            //recorre SeguimientoEsbirros y para cuando encuetre uno que no sea 0
            for (int i = 0; i < 20; i++) {
                if (AccionesPartidaMedia.SeguimientoEsbirros[0][i] == 0){
                    AccionesPartidaMedia.SeguimientoEsbirros[0][i] = cardId;
                    ubicacion = i;
                    break;
                }
            }
            idCartasTurno.set(posicion, idNuevo);
            AccionesPartidaMedia.SeguimientoEsbirros[1][ubicacion] = idNuevo;

        }
    }
    public static int ObtenerIDCarta(GameAction jugada){
        Pattern pattern = Pattern.compile("cardId:(\\d+)]");
        Pattern pattern1 = Pattern.compile("Attacker: \\[EntityReference id:(\\d+)]");
        Matcher matcher = pattern.matcher(jugada.toString());
        Matcher matcher1 = pattern1.matcher(jugada.toString());
        int cardId = 0;
        if (matcher.find()){
            String cardIdStr = matcher.group(1);
            cardId = Integer.parseInt(cardIdStr);
        }
        else if (matcher1.find()) {
            String cardIdStr = matcher1.group(1);
            cardId = Integer.parseInt(cardIdStr);
        }
        return cardId;
    }

    public static int transformarId(int id){
        //si el valor es mayor o igual a 68 buscamos ese id en SeguimientoEsbirros
        if (id >= 68) {
            for (int k = 0; k < 20; k++) {
                //si el id de la carta en la posicion j de idCartasPartida es igual al id de la carta en la posicion k de SeguimientoEsbirros
                if (id == AccionesPartidaMedia.SeguimientoEsbirros[1][k]) {
                    //si el valor de la matriz de turno en la columna i y fila j es igual al mejor valor
                    id = AccionesPartidaMedia.SeguimientoEsbirros[0][k];
                }
            }
        }
        //si el id de la carta esta entre el 37 y el 66, el id se resta 32
        if (id >= 37 && id <= 66) {
            id = id - 32;
        }
        if (id == 1 || id == 3 || id== 10){
            id = 9;
        }
        return id;
    }
}
