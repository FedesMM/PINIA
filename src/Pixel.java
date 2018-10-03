//Para lectura de archivo

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Pixel {
    int numero, productor, potrero, id;
    float[] cordenada = new float [2];
    float superficie, distanciaAlRio;
    String usoOriginal;

    //Esto no se va en la solucion
    // int[] decisiones = new int[Constantes.cantEstaciones]; //Se guarda el Uso*100+el numero de estacion de ese uso

    public Pixel(int numero, int id, int productor, int potrero, float[] cordenada, float superficie, float distanciaAlRio, String usoOriginal) {
        this.numero = numero;
        this.id = id;
        this.productor = productor;
        this.potrero = potrero;
        this.cordenada = cordenada;
        this.superficie = superficie;
        this.distanciaAlRio = distanciaAlRio;
        this.usoOriginal = usoOriginal;
    }

    public void imprimirPixel(){
            System.out.printf("\t("+this.numero+","+this.id+ ","+this.productor+","+this.potrero+
                    ", {"+this.cordenada[0]+","+this.cordenada[1]+"},"+
                    this.superficie+","+this.distanciaAlRio+","+this.usoOriginal+")%n");
    }
    public static void imprimirPixeles(){
        System.out.println("\t(numero, id, productor+, potrero, {coordenadas}, superficie, distanciaAlRio, usoOriginal)");
        System.out.println("For: "+Constantes.pixeles.length);
        for (int i = 0; i < Constantes.pixeles.length; i++) {
            System.out.println("i=" +i);
            Constantes.pixeles[i].imprimirPixel();
        }
        System.out.println("Salgo del for");
    }

    public static Pixel[] cargarPixelesTest(){
        Pixel[] pixeles=new Pixel[Constantes.cantPixeles];
        float[] cordenadaPixel;
        for (int iPixel = 0; iPixel < Constantes.cantPixeles; iPixel++) {
            cordenadaPixel=new float[]{iPixel,iPixel};
            pixeles[iPixel]=new Pixel(iPixel,iPixel,iPixel%2,iPixel, cordenadaPixel,iPixel,iPixel,"Prueba");
        }
        return pixeles;
    }
    public static Pixel[] cargarPixeles(){
        //Se abre el archivo .dbf se pasa a un texto plano, se remplazan las ',' por ',' para que no haya problemas al pasarlo de strings a float
        //Se borran los usos que no queremos con reg exp: ^.*<Uso>.*$\n para los usos: Bajo, Buffer, Calle, Forestacion, Humedal, Monte Nativo, Tajamar,Tambo
        Pixel[] pixeles=new Pixel[Constantes.cantPixeles];
        int  iPixel=0, id, productor, potrero;
        float superficie, distanciaAlRio;
        float[] cordenadaPixel;
        String usoOriginal;
        // The name of the file to open.
        String fileName = "prueba.in";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            //Leo linea con el encabezado:
            //PROD	POT	ID	SUP_HA	USO
            line = bufferedReader.readLine();

            //Leo el resto de las lineas
            while((line = bufferedReader.readLine()) != null) {
                //System.out.println("Linea "+iPixel+":"+line);
                //Parceo la linea segun sus separadores
                String[] campos = line.split("\\t");
                if (campos.length>0) {
                    //Filtro las calles
                    if (!campos[0].equalsIgnoreCase("Calle")) {
                        //Filtro los usos que anulan los pixeles
                        if (!campos[4].equalsIgnoreCase("Calle") &&
                                !campos[4].equalsIgnoreCase("Buffer") &&
                                !campos[4].equalsIgnoreCase("Forestacion")) {

                            id = Integer.valueOf(campos[2]);
                            productor = Integer.valueOf(campos[0])-1;
                            //System.out.println("Cargo el pixel "+iPixel+" en el productor "+(productor));
                            Constantes.productores[productor].pixelesDelProductor.add(iPixel);

                            potrero = Integer.valueOf(campos[1]);
                            superficie = Float.valueOf(campos[3]);
                            Constantes.productores[productor].areaTotal+=superficie;
                            usoOriginal = campos[4];

                            //Siguiente version
                            cordenadaPixel = new float[]{0F, 0F};
                            distanciaAlRio = Float.valueOf(campos[13]);
                            //Creo un Pixel con sus datos
                            Pixel pixelNuevo = new Pixel(iPixel, id, productor, potrero, cordenadaPixel, superficie, distanciaAlRio, usoOriginal);
                            //Lo agrego al arreglo de pixeles
                            pixeles[iPixel] = pixelNuevo;

                            iPixel++;
                        }
                    }
                }
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return pixeles;
    }

}