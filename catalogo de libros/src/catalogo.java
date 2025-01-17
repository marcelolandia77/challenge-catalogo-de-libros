public class catalogo import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;

// Clase Libro
class Libro {
    private String titulo;
    private String autor;
    private int añoPublicacion;
    private String urlLibro;

    // Constructor
    public Libro(String titulo, String autor, int añoPublicacion, String urlLibro) {
        this.titulo = titulo;
        this.autor = autor;
        this.añoPublicacion = añoPublicacion;
        this.urlLibro = urlLibro;
    }

    // Métodos getter y setter
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAñoPublicacion() {
        return añoPublicacion;
    }

    public void setAñoPublicacion(int añoPublicacion) {
        this.añoPublicacion = añoPublicacion;
    }

    public String getUrlLibro() {
        return urlLibro;
    }

    public void setUrlLibro(String urlLibro) {
        this.urlLibro = urlLibro;
    }

    // Método para mostrar la información del libro
    public void mostrarInformacion() {
        System.out.println("Título: " + titulo + ", Autor: " + autor + ", Año de Publicación: " + añoPublicacion + ", URL: " + urlLibro);
    }
}

// Clase CatalogoLibros
class CatalogoLibros {
    private ArrayList<Libro> catalogo;

    // Constructor
    public CatalogoLibros() {
        catalogo = new ArrayList<>();
    }

    // Método para agregar un libro al catálogo
    public void agregarLibro(Libro libro) {
        catalogo.add(libro);
    }

    // Método para listar todos los libros
    public void listarLibros() {
        if (catalogo.isEmpty()) {
            System.out.println("No hay libros en el catálogo.");
        } else {
            for (Libro libro : catalogo) {
                libro.mostrarInformacion();
            }
        }
    }

    // Método para buscar un libro por título
    public void buscarLibro(String titulo) {
        boolean encontrado = false;
        for (Libro libro : catalogo) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                libro.mostrarInformacion();
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("Libro no encontrado.");
        }
    }
}

// Clase ApiGutendex
class ApiGutendex {
    private static final String API_URL = "https://gutendex.com/books/";

    // Método para obtener libros por autor
    public static void obtenerLibrosPorAutor(String autor, CatalogoLibros catalogo) {
        try {
            // Construir la URL de la consulta
            String urlString = API_URL + "?author=" + autor + "&languages=es";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Convertir la respuesta JSON a un objeto Java
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode booksNode = rootNode.path("results");

            if (booksNode.isArray()) {
                for (JsonNode book : booksNode) {
                    String title = book.path("title").asText();
                    String author = book.path("author").asText();
                    String urlBook = book.path("formats").path("text/plain").asText();
                    // Crear un libro y agregarlo al catálogo
                    Libro libro = new Libro(title, author, 0, urlBook);
                    catalogo.agregarLibro(libro);
                    System.out.println("Libro añadido: " + title + " por " + author);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Clase Main
public class Main {
    public static void main(String[] args) {
        // Crear el catálogo de libros
        CatalogoLibros catalogo = new CatalogoLibros();

        // Agregar libros manualmente
        Libro libro1 = new Libro("El Alquimista", "Paulo Coelho", 1988, "http://example.com");
        Libro libro2 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, "http://example.com");
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);

        // Listar los libros agregados
        System.out.println("Lista de libros locales:");
        catalogo.listarLibros();

        // Obtener libros de Gutendex por autor
        System.out.println("\nBuscando libros de autor 'Miguel de Cervantes' en Gutendex:");
        ApiGutendex.obtenerLibrosPorAutor("Miguel de Cervantes", catalogo);

        // Listar los libros después de agregar los de Gutendex
        System.out.println("\nLista de libros después de obtener desde Gutendex:");
        catalogo.listarLibros();
    }
}
{
}
