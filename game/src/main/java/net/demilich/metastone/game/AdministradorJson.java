package net.demilich.metastone.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdministradorJson {

    private ObjectMapper mapeadorObjeto; // Elimina static aquí

    public AdministradorJson() {
        mapeadorObjeto = new ObjectMapper();
        mapeadorObjeto.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void guardarDatos(List<Integer> numeros, int numeroSeparado, String rutaArchivo) {
        if (numeroSeparado >= 10) {
            numeroSeparado = 0;
        }
        try {
            AdministradorJson administradorJson = new AdministradorJson(); // Crear una instancia
            DatosGuardados datos = new DatosGuardados(numeros, numeroSeparado);
            administradorJson.mapeadorObjeto.writeValue(new File(rutaArchivo), datos);
            System.out.println("Datos guardados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static DatosGuardados cargarDatos(String rutaArchivo) {
        try {
            AdministradorJson administradorJson = new AdministradorJson(); // Crear una instancia
            if (rutaArchivo != null) {
                return administradorJson.mapeadorObjeto.readValue(new File(rutaArchivo), DatosGuardados.class);
            } else {
                System.err.println("La ruta del archivo es nula.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void calculosFinalesMejorValor(Double[][] matrizFinal, ArrayList<Integer> idCartasTurno ) {
        AdministradorJson administradorJson = new AdministradorJson();
        // Cargar los datos desde el archivo
        DatosGuardados datosGuardados = administradorJson.cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMejorValor.json");
        List<Integer> mejoresValores = new ArrayList<>();
        List<Integer> valoresActuales = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMejorValor.json").getNumeros();
        int numeroPartidas = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMejorValor.json").getNumeroSeparado();
        //recorremos la matriz de turno columna por columna
        for (int i = 0; i < idCartasTurno.size(); i++) {
            int valorNoNulo = -999999999;
            for (int j = 0; j < 20; j++) {
                //si la matriz de turno en la columna i y fila j no es nula
                if (matrizFinal[j][i] != null &&
                            matrizFinal[j][i] != Float.NEGATIVE_INFINITY &&
                            matrizFinal[j][i] != Float.POSITIVE_INFINITY ) {
                    if (matrizFinal[j][i] > valorNoNulo) {
                        //el mejor valor sera el valor de la matriz de turno en la columna i y fila j
                        valorNoNulo = matrizFinal[j][i].intValue();
                    }
                }
            }
            int SumaNueva;
            int NuevoPromedio;
            if (valorNoNulo == -999999999) {
                NuevoPromedio = valoresActuales.get(i);
            }
            else {
                int SumaActual = valoresActuales.get(i) * numeroPartidas;
                SumaNueva = valorNoNulo + SumaActual;
                NuevoPromedio = SumaNueva / (numeroPartidas + 1);
            }
            //imprime valores actuales
            System.out.println("El valor actual de la carta " + idCartasTurno.get(i) + " es: " + valoresActuales.get(i));
            mejoresValores.add(NuevoPromedio);
        }
        datosGuardados.setValoresCartas(mejoresValores);
        datosGuardados.setNumeroPartida(numeroPartidas + 1);

        // Guardar los datos actualizados
        administradorJson.guardarDatos(mejoresValores, numeroPartidas + 1, "game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMejorValor.json");
    }
    public static void calculosFinalesMedia(Double[][] matrizFinal, ArrayList<Integer> idCartasTurno ) {
        AdministradorJson administradorJson = new AdministradorJson();
        // Cargar los datos desde el archivo
        DatosGuardados datosGuardados = administradorJson.cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMedia.json");
        List<Integer> mejoresValores = new ArrayList<>();
        List<Integer> valoresActuales = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMedia.json").getNumeros();
        int numeroPartidas = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMedia.json").getNumeroSeparado();
        //recorremos la matriz de turno columna por columna
        for (int i = 0; i < idCartasTurno.size(); i++) {
            int valorNoNulo = -999999999;
            int valorMedio = 0;
            int ayuda = 0;
            for (int j = 0; j < 20; j++) {
                //si la matriz de turno en la columna i y fila j no es nula
                if (matrizFinal[j][i] != null &&
                        matrizFinal[j][i] != Float.NEGATIVE_INFINITY &&
                        matrizFinal[j][i] != Float.POSITIVE_INFINITY ) {
                    //el mejor valor sera el valor de la matriz de turno en la columna i y fila j
                    valorNoNulo = matrizFinal[j][i].intValue();
                    valorMedio += matrizFinal[j][i].intValue();
                    ayuda++;

                }
            }
            int SumaNueva;
            int NuevoPromedio;
            if (valorNoNulo == -999999999) {
                NuevoPromedio = valoresActuales.get(i);
            }
            else {
                valorMedio = valorMedio / ayuda;
                int SumaActual = valoresActuales.get(i) * numeroPartidas;
                SumaNueva = valorMedio + SumaActual;
                NuevoPromedio = SumaNueva / (numeroPartidas + 1);
            }
            //imprime valores actuales
            System.out.println("El valor actual de la carta " + idCartasTurno.get(i) + " es: " + valoresActuales.get(i));
            mejoresValores.add(NuevoPromedio);
        }
        datosGuardados.setValoresCartas(mejoresValores);
        datosGuardados.setNumeroPartida(numeroPartidas + 1);

        // Guardar los datos actualizados
        administradorJson.guardarDatos(mejoresValores, numeroPartidas + 1, "game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMedia.json");
    }

    public static int ObtenerUnValorMejorValor(int idCarta) {
        List<Integer> valores = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMejorValor.json").getNumeros();
        return valores.get(idCarta-5);
    }
    public static int ObtenerUnValorMedia(int idCarta) {
        List<Integer> valores = cargarDatos("game\\src\\main\\java\\net\\demilich\\metastone\\game\\FicherosJson\\MediaCartasGuerreroControlMedia.json").getNumeros();
        return valores.get(idCarta-5);
    }

    static class DatosGuardados {
        private List<Integer> ValoresCartas;
        private int NumeroPartida;

        public DatosGuardados() {
            // Constructor vacío necesario para la deserialización de JSON
        }

        public DatosGuardados(@JsonProperty("numeros") List<Integer> ValoresCartas,
                              @JsonProperty("numeroSeparado") int NumeroPartida) {
            this.ValoresCartas = ValoresCartas;
            this.NumeroPartida = NumeroPartida;
        }

        public List<Integer> getNumeros() {
            return ValoresCartas;
        }

        public int getNumeroSeparado() {
            return NumeroPartida;
        }

        public void setValoresCartas(List<Integer> valoresCartas) {
            this.ValoresCartas = valoresCartas;
        }

        public void setNumeroPartida(int numeroPartida) {
            this.NumeroPartida = numeroPartida;
        }
    }
}
