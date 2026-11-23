
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// HERENCIA
abstract class Persona {
    private final String nombre;
    private final String ci;

    protected Persona(String nombre, String ci) {
        this.nombre = nombre;
        this.ci = ci;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCi() {
        return ci;
    }

    public abstract String toString();
}

// ESTUDIANTE
class Estudiante extends Persona {
    private final String codigo;
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public Estudiante(String nombre, String ci, String codigo) {
        super(nombre, ci);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public Inscripcion inscribirse(Materia materia) {
        for (Inscripcion i : inscripciones) {
            if (i.getMateria() == materia) {
                throw new IllegalArgumentException(
                    "El estudiante ya está inscrito en esta materia"
                );
            }
        }

        Inscripcion inscripcion =
            new Inscripcion(this, materia, LocalDate.now());

        inscripciones.add(inscripcion);
        materia.registrarInscripcion(inscripcion);

        return inscripcion;
    }

    public List<Inscripcion> getInscripciones() {
        return Collections.unmodifiableList(inscripciones);
    }

    @Override
    public String toString() {
        return "Estudiante " + getNombre() + " (codigo " + codigo + ")";
    }
}

// DOCENTE
class Docente extends Persona {
    private final String especialidad;
    private final List<Materia> materias = new ArrayList<>();
    private final List<Ayudante> ayudantes = new ArrayList<>();
    private Carrera carrera;

    public Docente(String nombre, String ci, String especialidad) {
        super(nombre, ci);
        this.especialidad = especialidad;
    }

    public void dictar(Materia materia) {
        if (materia.getDocente() != null) {
            throw new IllegalArgumentException(
                "La materia ya tiene un docente"
            );
        }

        materias.add(materia);
        materia.setDocente(this);
    }

    public void asignarCarrera(Carrera carrera) {
        if (this.carrera != null && this.carrera != carrera) {
            throw new IllegalArgumentException(
                "El docente ya pertenece a otra carrera"
            );
        }

        this.carrera = carrera;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void agregarAyudante(Ayudante ayudante) {
        ayudantes.add(ayudante);
    }

    public Acta generarActa(Materia materia) {
        return new Acta(materia);
    }

    public List<Materia> getMaterias() {
        return Collections.unmodifiableList(materias);
    }

    @Override
    public String toString() {
        return "Docente " + getNombre() + " (" + especialidad + ")";
    }
}

// COMPOSICION: UNIVERSIDAD - CARRERA
class Universidad {
    private final String nombre;
    private final List<Carrera> carreras = new ArrayList<>();

    public Universidad(String nombre) {
        this.nombre = nombre;
    }

    public Carrera crearCarrera(String nombreCarrera) {
        Carrera carrera = new Carrera(nombreCarrera);
        carreras.add(carrera);
        return carrera;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Carrera> getCarreras() {
        return Collections.unmodifiableList(carreras);
    }
}

// AGREGACION: CARRERA - DOCENTE
class Carrera {
    private final String nombre;
    private final List<Docente> docentes = new ArrayList<>();

    Carrera(String nombre) {
        this.nombre = nombre;
    }

    public void agregarDocente(Docente docente) {
        if (docente.getCarrera() != null
                && docente.getCarrera() != this) {
            throw new IllegalArgumentException(
                "El docente ya pertenece a otra carrera"
            );
        }

        if (!docentes.contains(docente)) {
            docentes.add(docente);
            docente.asignarCarrera(this);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public List<Docente> getDocentes() {
        return Collections.unmodifiableList(docentes);
    }
}

// MATERIA Y HORARIO
class Materia {
    private final String codigo;
    private final int creditos;
    private Docente docente;

    private final List<Horario> horarios = new ArrayList<>();
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public Materia(String codigo, int creditos) {
        this.codigo = codigo;
        this.creditos = creditos;
    }

    public Horario agregarHorario(LocalDate fecha, String aula) {
        Horario horario = new Horario(fecha, aula);
        horarios.add(horario);
        return horario;
    }

    void registrarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }

    void setDocente(Docente docente) {
        this.docente = docente;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCreditos() {
        return creditos;
    }

    public Docente getDocente() {
        return docente;
    }

    public List<Horario> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    public List<Inscripcion> getInscripciones() {
        return Collections.unmodifiableList(inscripciones);
    }

    // COMPOSICION ESTRICTA: HORARIO SOLO SE CREA DESDE MATERIA
    public static class Horario {
        private final LocalDate fecha;
        private final String aula;

        private Horario(LocalDate fecha, String aula) {
            this.fecha = fecha;
            this.aula = aula;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public String getAula() {
            return aula;
        }
    }
}

// INTERFAZ
interface Calificable {
    double calcularNota();
}

// INSCRIPCION
class Inscripcion implements Calificable {
    private final Estudiante estudiante;
    private final Materia materia;
    private final LocalDate fecha;
    private double nota = -1;

    Inscripcion(Estudiante estudiante, Materia materia, LocalDate fecha) {
        this.estudiante = estudiante;
        this.materia = materia;
        this.fecha = fecha;
    }

    public void registrarNota(double nota) {
        if (nota < 0 || nota > 100) {
            throw new IllegalArgumentException(
                "La nota debe estar entre 0 y 100"
            );
        }

        this.nota = nota;
    }

    @Override
    public double calcularNota() {
        if (nota == -1) {
            throw new IllegalStateException("Nota no registrada");
        }

        return nota;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Materia getMateria() {
        return materia;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}

// ACTA
class Acta {
    private final LocalDate fecha = LocalDate.now();
    private final String contenido;

    Acta(Materia materia) {
        StringBuilder sb = new StringBuilder();

        sb.append("ACTA ")
          .append(materia.getCodigo())
          .append(" - ")
          .append(fecha)
          .append("\n");

        for (Inscripcion i : materia.getInscripciones()) {
            sb.append(i.getEstudiante().getNombre())
              .append(" : ")
              .append(i.calcularNota())
              .append("\n");
        }

        contenido = sb.toString();
    }

    @Override
    public String toString() {
        return contenido;
    }
}

// AYUDANTE: HEREDA DE ESTUDIANTE Y SE ASOCIA CON DOCENTE
class Ayudante extends Estudiante {
    private final Docente docente;

    public Ayudante(String nombre, String ci, String codigo,
                    Docente docente) {
        super(nombre, ci, codigo);
        this.docente = docente;
        docente.agregarAyudante(this);
    }

    public Docente getDocente() {
        return docente;
    }
}

// PROGRAMA PRINCIPAL
public class SistemaAcademico {

    public static void main(String[] args) {

        // UNIVERSIDAD Y CARRERA
        Universidad umsa = new Universidad("UMSA");
        Carrera informatica = umsa.crearCarrera("Informatica");

        // DOCENTE
        Docente docente = new Docente(
            "Ana Alba", "1234567", "Ingenieria de Software"
        );

        informatica.agregarDocente(docente);

        // MATERIA
        Materia poo = new Materia("INF-121", 6);
        docente.dictar(poo);

        // HORARIOS
        poo.agregarHorario(
            LocalDate.of(2026, 7, 2), "Aula 101"
        );

        poo.agregarHorario(
            LocalDate.of(2026, 7, 9), "Aula 101"
        );

        // ESTUDIANTES
        Estudiante e1 = new Estudiante(
            "Carlos Flores", "7654321", "E-001"
        );

        Estudiante e2 = new Estudiante(
            "Maria Salas", "7654322", "E-002"
        );

        // INSCRIPCIONES
        Inscripcion i1 = e1.inscribirse(poo);
        Inscripcion i2 = e2.inscribirse(poo);

        // REGISTRO DE NOTAS
        i1.registrarNota(85);
        i2.registrarNota(92);

        // AYUDANTE
        Ayudante ayudante = new Ayudante(
            "Pedro Lopez", "7654323", "E-003", docente
        );

        // HERENCIA Y POLIMORFISMO
        List<Persona> personas = Arrays.asList(
            docente, e1, e2, ayudante
        );

        personas.forEach(p -> System.out.println(p));

        // INTERFAZ CALIFICABLE
        Calificable c = i1;

        System.out.println(
            "Nota de " + e1.getNombre() + " : " + c.calcularNota()
        );

        // GENERAR ACTA
        Acta acta = docente.generarActa(poo);

        System.out.println(acta);
    }
}
