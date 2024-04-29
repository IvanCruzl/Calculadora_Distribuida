package tareas;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Producto implements Serializable {
    private String nombre;
    private double precio;
    private int cantidadEnStock;

    public Producto(String nombre, double precio, int cantidadEnStock) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadEnStock = cantidadEnStock;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidadEnStock=" + cantidadEnStock +
                '}';
    }
}

public class Serializacion {
    public static void main(String[] args) {
        // Crear algunos productos
        Producto producto1 = new Producto("Canelitas", 10.99, 50);
        Producto producto2 = new Producto("Cheetos", 20.49, 30);
        Producto producto3 = new Producto("Oreos", 5.99, 100);

        // Crear una lista de productos y añadir los productos
        List<Producto> listaProductos = new ArrayList<>();
        listaProductos.add(producto1);
        listaProductos.add(producto2);
        listaProductos.add(producto3);

        // Serializar la lista de productos y guardarla en un archivo
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("productos.ser"))) {
            outputStream.writeObject(listaProductos);
            System.out.println("Lista de productos serializada y guardada en productos.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Leer y deserializar la lista de productos desde el archivo
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("productos.ser"))) {
            List<Producto> productosDeserializados = (List<Producto>) inputStream.readObject();
            System.out.println("Lista de productos deserializada desde productos.ser:");
            for (Producto producto : productosDeserializados) {
                System.out.println(producto);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
