package ar.edu.uade.cliente;

import ar.edu.uade.gym.GymException;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;

public class VistaPrincipalClaseAsignada extends JFrame {

    private ControladorCliente controller;

	public VistaPrincipalClaseAsignada() {
		super("Cliente: Clases Asignadas");
        this.controller = ControladorCliente.getInstance();

        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        JPanel panelMenu = new JPanel();
		panelMenu.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		/* Ejercicios Disponibles por Sedes*/
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		JButton btnClaseAsignada = new JButton("Clases Asignadas");
		panelMenu.add(btnClaseAsignada, gbc);
		
		/* Ejercicios Disponibles por Sedes*/
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JButton btnEjerciciosSedes = new JButton("Ejercicios Disponibles por Sedes");
		panelMenu.add(btnEjerciciosSedes, gbc);
		
		/* Membresia */
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JButton btnMembresia = new JButton("Membresia");
		panelMenu.add(btnMembresia, gbc);
		
		//Titulo
        JPanel panel = new JPanel();
		JLabel labelTitulo = new JLabel("Clases Asignadas");
		labelTitulo.setFont(new Font("Arial", Font.BOLD, 32));
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTH;
		panelMenu.add(labelTitulo, gbc);
		
		gbc.gridwidth = 1;

		this.add(panelMenu, BorderLayout.NORTH);
		
		JPanel panelContenido = new JPanel(new BorderLayout());
		
		// Tabla de clases asignadas
		JTable tabla = new JTable();
		DefaultTableModel modelo = new DefaultTableModel();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] header = new String[8];
        ArrayList<LocalDate> semana = new ArrayList<LocalDate>();
        header[0] = "Horario";
        for (int day=1; day<=7; day++) {
            LocalDate fecha = LocalDate.now().plusDays(day-1);
            semana.add(fecha);
            header[day] = getDiaSemana(fecha.getDayOfWeek().getValue()) + " " + fecha.format(formatter);
        }

        modelo.setColumnIdentifiers(header);

        LocalTime[] horas = {
                LocalTime.of(7,0),
                LocalTime.of(8,0),
                LocalTime.of(9,0),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                LocalTime.of(12,0),
                LocalTime.of(13,0),
                LocalTime.of(14,0),
                LocalTime.of(15,0),
                LocalTime.of(16,0),
                LocalTime.of(17,0),
                LocalTime.of(18,0),
                LocalTime.of(19,0),
                LocalTime.of(20,0),
                LocalTime.of(21,0)};

        int cantColumnas = header.length;
        int cantFilas = horas.length;

        HashMap<LocalDate, ArrayList<LocalTime>>
                horariosOcupados = controller.getClasesAsignadas("belgrano");

        DateTimeFormatter horasFormatter = DateTimeFormatter.ofPattern("HH");

        for (int i = 1; i <= cantFilas-1; i++) {
            String[] horarioDisponible = new String[cantColumnas+1];
            horarioDisponible[0] = horas[i].format(horasFormatter) + "-" + horas[i].plusHours(1).format(horasFormatter);
            for (int j = 1; j <= cantColumnas-1; j++) {
                LocalDate dia = semana.get(j - 1);
                LocalTime horarioClase = horas[i];
                if (horarioClase != null && contieneEjercicio(horariosOcupados, dia, horarioClase)) {
                    horarioDisponible[j] = "Ocupado";
                } else {

                    horarioDisponible[j] = "Libre";
                }
            }
            modelo.addRow(horarioDisponible);
        }
		
		tabla.setModel(modelo);
		
        for (int i = 0; i < 4; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        // Agregar la tabla a un JScrollPane y añadirlo a la ventana
        JScrollPane scrollPane = new JScrollPane(tabla);
        panelContenido.add(scrollPane, BorderLayout.CENTER);


        // Inscripcion
        JPanel panelInscripcion = new JPanel();
        panelContenido.add(panelInscripcion, BorderLayout.SOUTH);
        String sede = "Belgrano";

        HashMap<String, ArrayList<LocalDateTime>> clasesExistentes = controller.getClasesPorSede(sede);

        panelInscripcion.setLayout(new GridBagLayout());
        GridBagConstraints gbcInscripcion = new GridBagConstraints();
        gbcInscripcion.insets = new Insets(5, 5, 5, 5);
        gbcInscripcion.anchor = GridBagConstraints.WEST;

        List<JComboBox<String>> comboBoxesHorarios = new ArrayList<>();
        int i = 0;
        for (String clase : clasesExistentes.keySet()) {
            gbcInscripcion.gridx = 0;
            gbcInscripcion.gridy = i;
            JLabel labelClase = new JLabel(clase + ":");
            panelInscripcion.add(labelClase, gbcInscripcion);

            gbcInscripcion.gridx = 1;
            gbcInscripcion.gridy = i;
            JComboBox<String> comboBoxHorarios = new JComboBox<>();
            comboBoxHorarios.addItem("---"); // Opción por defecto
            ArrayList<LocalDateTime> horarios = clasesExistentes.get(clase);
            for (LocalDateTime horario : horarios) {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
                String fechaHoraFormateada = horario.format(formato);
                comboBoxHorarios.addItem(fechaHoraFormateada);
            }
            panelInscripcion.add(comboBoxHorarios, gbcInscripcion);
            comboBoxesHorarios.add(comboBoxHorarios);
            i++;
        }

        JButton btnInscribirse = new JButton("Inscribirse");
        gbcInscripcion.gridx = 1;
        gbcInscripcion.gridy = i;
        gbcInscripcion.gridwidth = 2;
        panelInscripcion.add(btnInscribirse, gbcInscripcion);

        this.add(panelContenido, BorderLayout.CENTER);

        //Boton Incripcion
        btnInscribirse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < comboBoxesHorarios.size(); i++) {
                    String clase = clasesExistentes.keySet().toArray(new String[0])[i];
                    JComboBox<String> comboBoxHorarios = comboBoxesHorarios.get(i);
                    String horarioSeleccionado = (String) comboBoxHorarios.getSelectedItem();
                    if (!horarioSeleccionado.equals("---")){
                        try {
                            controller.inscibirAlumno(clase,horarioSeleccionado,sede);
                        } catch (GymException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.out.println("Clase: " + clase + ", Horario: " + horarioSeleccionado);
                    }
                }
            }
        });

        //Botones Menu
        class HandlerBtnEjerciciosSedes implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.abrirVistaEjercicioPorSede();
                //controladorLogin.validarUsuarioExistente(campoUsuario.getText(), campoContrasenia.getText());
            }
        }

		/*INSTANCIACION DEL MANEJADOR*/
		HandlerBtnEjerciciosSedes handlerBtnEjerciciosSedes=new HandlerBtnEjerciciosSedes();

		/*ASIGNACION DEL MANEJADOR AL BOTON*/
		btnEjerciciosSedes.addActionListener(handlerBtnEjerciciosSedes);

		//Boton Membresia
        class HandlerBtnMembresia implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.abrirVistaMembresia();
            }
        }

		/*INSTANCIACION DEL MANEJADOR*/
		HandlerBtnMembresia handlerBtnMembresia=new HandlerBtnMembresia();

		/*ASIGNACION DEL MANEJADOR AL BOTON*/
		btnMembresia.addActionListener(handlerBtnMembresia);
    }

    private boolean contieneEjercicio(HashMap<LocalDate, ArrayList<LocalTime>> horariosOcupados,LocalDate dia, LocalTime hora) {
        boolean flagOcupado = false;
        for (LocalTime horarioOcupado : horariosOcupados.get(dia)) {
            if (horarioOcupado.equals(hora)) {
                flagOcupado = true;
            }
        }
        return flagOcupado;
    }
    
    private String nombreEjercicio(String[] ejercicios, String ejercicio) {
        for (String e : ejercicios) {
            if (e.equals(ejercicio)) {
                return ejercicios[1];
            }
        }
        return null;
    }

    private String getDiaSemana(int dia) {
        return switch (dia) {
            case 1 -> "Lun";
            case 2 -> "Mar";
            case 3 -> "Mie";
            case 4 -> "Jue";
            case 5 -> "Vie";
            case 6 -> "Sab";
            case 7 -> "Dom";
            default -> "xxx";
        };
    }


}
