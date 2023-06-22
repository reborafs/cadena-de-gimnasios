package ar.edu.uade.usuarios;

import java.util.ArrayList;
import ar.edu.uade.gym.Clase;

public class Profesor extends Usuario {

    private double sueldo;

    public Profesor(String nombre, double sueldo) {
    	super(nombre);
        this.sueldo = sueldo;
    }
	
	public ArrayList<Clase> getClasesAsignadas() {
        // Code for getting assigned classes
        return null;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }

    public double getSueldo() {
        return this.sueldo;
    }
	
	@Override
    public boolean soyProfesor() {
    	return true;
    }


}
