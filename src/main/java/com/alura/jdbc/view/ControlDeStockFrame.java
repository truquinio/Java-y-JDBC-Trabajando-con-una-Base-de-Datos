package com.alura.jdbc.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.alura.jdbc.controller.CategoriaController;
import com.alura.jdbc.controller.ProductoController;
import com.alura.jdbc.modelo.Producto;

public class ControlDeStockFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  private JLabel labelNombre, labelDescripcion, labelCantidad, labelCategoria;
  private JTextField textoNombre, textoDescripcion, textoCantidad;
  private JComboBox<Object> comboCategoria;
  private JButton botonGuardar, botonModificar, botonLimpiar, botonEliminar, botonReporte;
  private JTable tabla;
  private DefaultTableModel modelo;
  private ProductoController productoController;
  private CategoriaController categoriaController;

  public ControlDeStockFrame() {
    super("Productos");

    this.categoriaController = new CategoriaController();
    this.productoController = new ProductoController();

    Container container = getContentPane();
    setLayout(null);

    configurarCamposDelFormulario(container);

    configurarTablaDeContenido(container);

    configurarAccionesDelFormulario();
  }

  private void configurarTablaDeContenido(Container container) {
    tabla = new JTable();

    modelo = (DefaultTableModel) tabla.getModel();
    modelo.addColumn("Identificador del Producto");
    modelo.addColumn("Nombre del Producto");
    modelo.addColumn("Descripción del Producto");
    modelo.addColumn("Cantidad Disponible");

    cargarTabla();

    tabla.setBounds(10, 205, 760, 280);

    botonEliminar = new JButton("Eliminar");
    botonModificar = new JButton("Modificar");
    botonReporte = new JButton("Ver Reporte");
    botonEliminar.setBounds(10, 500, 80, 20);
    botonModificar.setBounds(100, 500, 80, 20);
    botonReporte.setBounds(190, 500, 80, 20);

    container.add(tabla);
    container.add(botonEliminar);
    container.add(botonModificar);
    container.add(botonReporte);

    setSize(800, 600);
    setVisible(true);
    setLocationRelativeTo(null);
  }

  private void configurarCamposDelFormulario(Container container) {
    labelNombre = new JLabel("Nombre del Producto");
    labelDescripcion = new JLabel("Descripción del Producto");
    labelCantidad = new JLabel("Cantidad");
    labelCategoria = new JLabel("Categoría del Producto");

    labelNombre.setBounds(10, 10, 240, 15);
    labelDescripcion.setBounds(10, 50, 240, 15);
    labelCantidad.setBounds(10, 90, 240, 15);
    labelCategoria.setBounds(10, 130, 240, 15);

    labelNombre.setForeground(Color.BLACK);
    labelDescripcion.setForeground(Color.BLACK);
    labelCategoria.setForeground(Color.BLACK);

    textoNombre = new JTextField();
    textoDescripcion = new JTextField();
    textoCantidad = new JTextField();
    comboCategoria = new JComboBox<>();
    comboCategoria.addItem("Elige una Categoría");

    // TODO
    var categorias = this.categoriaController.listar();
    // categorias.forEach(categoria -> comboCategoria.addItem(categoria.getNombre()));

    textoNombre.setBounds(10, 25, 265, 20);
    textoDescripcion.setBounds(10, 65, 265, 20);
    textoCantidad.setBounds(10, 105, 265, 20);
    comboCategoria.setBounds(10, 145, 265, 20);

    botonGuardar = new JButton("Guardar");
    botonLimpiar = new JButton("Limpiar");
    botonGuardar.setBounds(10, 175, 80, 20);
    botonLimpiar.setBounds(100, 175, 80, 20);

    container.add(labelNombre);
    container.add(labelDescripcion);
    container.add(labelCantidad);
    container.add(labelCategoria);
    container.add(textoNombre);
    container.add(textoDescripcion);
    container.add(textoCantidad);
    container.add(comboCategoria);
    container.add(botonGuardar);
    container.add(botonLimpiar);
  }

  private void configurarAccionesDelFormulario() {
    botonGuardar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        guardar();
        limpiarTabla();
        cargarTabla();
      }
    });

    botonLimpiar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        limpiarFormulario();
      }
    });

    botonEliminar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        eliminar();
        limpiarTabla();
        cargarTabla();
      }
    });

    botonModificar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        modificar();
        limpiarTabla();
        cargarTabla();
      }
    });

    botonReporte.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        abrirReporte();
      }
    });
  }

  private void abrirReporte() {
    new ReporteFrame(this);
  }

  private void limpiarTabla() {
    // modelo.getDataVector().clear();
    modelo.setRowCount(0);
  }

  private boolean tieneFilaElegida() {
    return tabla.getSelectedRowCount() == 0 || tabla.getSelectedColumnCount() == 0;
  }

  private void modificar() {

    // Si no se ha elegido una fila, se muestra un mensaje de advertencia y se sale
    // del método.
    if (tieneFilaElegida()) {
      JOptionPane.showMessageDialog(this, "Por favor, elije un item");
      return;
    }

    // Se utiliza Optional.ofNullable para evitar NullPointerException.
    // Se obtiene el valor de la celda seleccionada y se procesa si no es nulo.
    Optional.ofNullable(modelo.getValueAt(tabla.getSelectedRow(), tabla.getSelectedColumn()))
        .ifPresentOrElse(fila -> {

          // Se obtiene el valor del id de la fila seleccionada.
          Integer id = Integer.valueOf(modelo.getValueAt(tabla.getSelectedRow(), 0).toString());


          // Se obtienen los valores del nombre, descripción y cantidad de la fila
          // seleccionada.
          String nombre = (String) modelo.getValueAt(tabla.getSelectedRow(), 1);
          String descripcion = (String) modelo.getValueAt(tabla.getSelectedRow(), 2);
          Integer cantidad = Integer.valueOf(modelo.getValueAt(tabla.getSelectedRow(), 3).toString());

          int filasModificadas;

          // Se llama al método modificar del controlador del producto, pasando los
          // valores obtenidos.
          try {
            filasModificadas = this.productoController.modificar(nombre, descripcion, cantidad, id);
          } catch (SQLException e) {
            e.printStackTrace();

            // Se lanza una RuntimeException si se produce una SQLException.
            throw new RuntimeException(e);
          }

          // Se muestra un mensaje de éxito con la cantidad de filas modificadas.
          JOptionPane.showMessageDialog(this, String.format("%d item modificado con éxito!", filasModificadas));
        }, () -> JOptionPane.showMessageDialog(this, "Por favor, elije un item"));
  }

  private void eliminar() {

    // Verifica si se ha seleccionado una fila de la tabla
    if (tieneFilaElegida()) {
      JOptionPane.showMessageDialog(this, "Por favor, elije un item");
      return;
    }

    // Obtiene el valor de la celda seleccionada de la tabla, si es nulo muestra un
    // mensaje de error.
    Optional.ofNullable(modelo.getValueAt(tabla.getSelectedRow(), tabla.getSelectedColumn()))
        .ifPresentOrElse(fila -> {

          // Obtiene el id del elemento a eliminar
          Integer id = Integer.valueOf(modelo.getValueAt(tabla.getSelectedRow(), 0).toString());

          int cantidadEliminada;

          try {
            // Ejecuta el método para eliminar el elemento seleccionado de la base de datos
            cantidadEliminada = this.productoController.eliminar(id);

          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          // Elimina la fila seleccionada de la tabla
          modelo.removeRow(tabla.getSelectedRow());

          JOptionPane.showMessageDialog(this, cantidadEliminada + " Item eliminado con éxito!");
        }, () -> JOptionPane.showMessageDialog(this, "Por favor, elije un item"));
  }

  private void cargarTabla() {
    try {
      // Se obtiene una lista de productos a partir de la consulta SQL
      var productos = this.productoController.listar();

      try {
        // Se recorre la lista de productos obtenidos y se agrega una fila por cada uno
        // de ellos en la tabla, incluyendo la descripción del producto
        productos.forEach(producto -> modelo.addRow(new Object[] { producto.get("ID"), producto.get("NOMBRE"),
            producto.get("DESCRIPCION"), producto.get("CANTIDAD") }));

      } catch (Exception e) {
        // Se lanza una excepción en caso de error
        throw e;
      }
    } catch (SQLException e) {
      // Se lanza una excepción en caso de error
      throw new RuntimeException(e);
    }
  }

  private void guardar() {
    if (textoNombre.getText().isBlank() || textoDescripcion.getText().isBlank()) {
      JOptionPane.showMessageDialog(this, "Los campos Nombre y Descripción son requeridos.");
      return;
    }

    Integer cantidadInt;

    try {
      cantidadInt = Integer.parseInt(textoCantidad.getText());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, String
          .format("El campo cantidad debe ser numérico dentro del rango %d y %d.", 0, Integer.MAX_VALUE));
      return;
    }

    var producto = new Producto(textoNombre.getText(), textoDescripcion.getText(), cantidadInt);

    try {
      this.productoController.guardar(producto);
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    JOptionPane.showMessageDialog(this, "Registrado con éxito!");

    this.limpiarFormulario();
  }

  private void limpiarFormulario() {
    this.textoNombre.setText("");
    this.textoDescripcion.setText("");
    this.textoCantidad.setText("");
    this.comboCategoria.setSelectedIndex(0);
  }
}



/*
 * private void guardar() {
    if (textoNombre.getText().isBlank() || textoDescripcion.getText().isBlank()) {
      JOptionPane.showMessageDialog(this, "Los campos Nombre y Descripción son requeridos.");
      return;
    }

    Integer cantidadInt;

    try {
      cantidadInt = Integer.parseInt(textoCantidad.getText());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, String
          .format("El campo cantidad debe ser numérico dentro del rango %d y %d.", 0, Integer.MAX_VALUE));
      return;
    }

    var producto = new HashMap<String, String>();
    producto.put("NOMBRE", textoNombre.getText());
    producto.put("DESCRIPCION", textoDescripcion.getText());
    producto.put("CANTIDAD", String.valueOf(cantidadInt));
    var categoria = comboCategoria.getSelectedItem();

    try {
      this.productoController.guardar(producto);
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    JOptionPane.showMessageDialog(this, "Registrado con éxito!");

    this.limpiarFormulario();
  }
 */