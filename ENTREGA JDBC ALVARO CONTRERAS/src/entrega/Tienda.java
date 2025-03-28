package entrega;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ConnectionSingleton {
	private static Connection con;

	public static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://127.0.0.1:3307/TIENDA_JBDC";
		String user = "alumno";
		String password = "alumno";

		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection(url, user, password);
		}
		return con;
	}
}

public class Tienda {

	private JFrame frame;
	private JTextField textField_ID;
	private JTextField textField_Desc;
	private JTextField textField_Venta;
	private JTextField textField_Almacen;
	private JTextField textField_Total;

	boolean comprobarExpReg(String cadena, String patron) {
		Pattern pat = Pattern.compile(patron);
		Matcher mat = pat.matcher(cadena);

		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tienda window = new Tienda();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Tienda() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 881, 559);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		DefaultTableModel model_Producto = new DefaultTableModel();
		model_Producto.addColumn("Cod_Prod");
		model_Producto.addColumn("Desc_Prod");
		model_Producto.addColumn("Un_venta");
		model_Producto.addColumn("Un_almacen");
		model_Producto.addColumn("Un_total");

		try {
			Connection con = ConnectionSingleton.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Productos");
			while (rs.next()) {
				Object[] row = new Object[5];
				row[0] = rs.getString("Cod_Prod");
				row[1] = rs.getString("Desc_Prod");
				row[2] = rs.getInt("Un_venta");
				row[3] = rs.getString("Un_almacen");
				row[4] = rs.getString("Un_total");
				model_Producto.addRow(row);
			}
		} catch (SQLException ex) {
		}

		JButton btnAnadir = new JButton("Añadir");
		btnAnadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Comprobar que los textfields no esten vacios
				if (textField_Desc.getText().length() == 0) {
					JOptionPane.showMessageDialog(frame, "La descripcion está vacia", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				} else if (textField_Venta.getText().length() == 0) {
					JOptionPane.showMessageDialog(frame, "El número de unidades esta vacio", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				} else if (textField_Almacen.getText().length() == 0) {
					JOptionPane.showMessageDialog(frame, "El número de unidades esta vacio", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				}

				// Comprobar que los textfields tengan valores correctos
				if (!comprobarExpReg(textField_Desc.getText(), "^[a-zA-z]+$")) {
					JOptionPane.showMessageDialog(frame, "La descripcion debe contener solo letras", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				} else if (!comprobarExpReg(textField_Venta.getText(), "^\\d+$")) {
					JOptionPane.showMessageDialog(frame, "Las unidades 1 solo tienen numeros", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				} else if (!comprobarExpReg(textField_Almacen.getText(), "^\\d+$")) {
					JOptionPane.showMessageDialog(frame, "Las unidades 2 solo tienen numeros", "Advertencia",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// Añadir datos del preducto
					try {
						Connection con = ConnectionSingleton.getConnection();
						PreparedStatement insProd = con.prepareStatement(
								"INSERT INTO Productos (Desc_Prod,Un_venta,Un_almacen, Un_total) VALUES (?,?,?,?)");
						String descipcion = textField_Desc.getText();
						String univenta = textField_Venta.getText();
						String unialmacen = textField_Almacen.getText();
						int uni1 = Integer.parseInt(univenta);
						int uni2 = Integer.parseInt(unialmacen);
						int unidtotal = uni1 + uni2;
						String s = String.valueOf(unidtotal); 	
						textField_Total.setText(s);
						String unitotal = textField_Total.getText();
						insProd.setString(1, descipcion);
						insProd.setString(2, univenta);
						insProd.setString(3, unialmacen);
						insProd.setString(4, unitotal);
						insProd.executeUpdate();
						JOptionPane.showMessageDialog(frame, "Se ha añadido el producto");
						insProd.close();
						Statement muestraprod = con.createStatement();
						ResultSet visualizarprodusctos = muestraprod
								.executeQuery("SELECT * FROM Productos ORDER BY Cod_Prod");
						model_Producto.setRowCount(0);
						while (visualizarprodusctos.next()) {
							Object[] row = new Object[5];
							row[0] = visualizarprodusctos.getString("Cod_Prod");
							row[1] = visualizarprodusctos.getString("Desc_Prod");
							row[2] = visualizarprodusctos.getInt("Un_venta");
							row[3] = visualizarprodusctos.getString("Un_almacen");
							row[4] = visualizarprodusctos.getString("Un_total");
							model_Producto.addRow(row);
						}
						con.close();

					} catch (SQLException erra) {
						erra.printStackTrace();
						erra.getMessage();
						erra.getSQLState();
						JOptionPane.showMessageDialog(frame, erra.getMessage(), "Advertencia",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JTable table_Productos = new JTable(model_Producto);
		table_Productos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indice = table_Productos.getSelectedRow();
				TableModel modelo = table_Productos.getModel();
				textField_ID.setText(String.valueOf(modelo.getValueAt(indice, 0)));
				textField_Desc.setText(String.valueOf(modelo.getValueAt(indice, 1)));
				textField_Venta.setText(String.valueOf(modelo.getValueAt(indice, 2)));
				textField_Almacen.setText(String.valueOf(modelo.getValueAt(indice, 3)));
				textField_Total.setText(String.valueOf(modelo.getValueAt(indice, 4)));
			}
		});
		table_Productos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table_Productos.setBounds(71, 56, 146, 74);
		JScrollPane scrollPane = new JScrollPane(table_Productos);
		scrollPane.setBounds(73, 62, 367, 188);
		frame.getContentPane().add(scrollPane);
		btnAnadir.setBounds(101, 418, 117, 25);
		frame.getContentPane().add(btnAnadir);

		JButton btnActualizar = new JButton("Actualizar");
		btnActualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Connection con = ConnectionSingleton.getConnection();

					Statement muestraprod = con.createStatement();
					ResultSet visualizarprodusctos = muestraprod
							.executeQuery("SELECT * FROM Productos ORDER BY Cod_Prod");
					model_Producto.setRowCount(0);
					while (visualizarprodusctos.next()) {
						Object[] row = new Object[5];
						row[0] = visualizarprodusctos.getString("Cod_Prod");
						row[1] = visualizarprodusctos.getString("Desc_Prod");
						row[2] = visualizarprodusctos.getInt("Un_venta");
						row[3] = visualizarprodusctos.getString("Un_almacen");
						row[4] = visualizarprodusctos.getString("Un_total");
						model_Producto.addRow(row);
					}
					con.close();

				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage(), "Advertencia", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnActualizar.setBounds(399, 418, 117, 25);
		frame.getContentPane().add(btnActualizar);

		JButton btnNewBorrar = new JButton("Borrar");
		btnNewBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int codp=table_Productos.getSelectedRow();
					TableModel model= table_Productos.getModel();
					
					Connection con =ConnectionSingleton.getConnection();
					PreparedStatement updCont = con.prepareStatement("DELETE FROM Productos WHERE Cod_Prod=?");
					updCont.executeUpdate();
					updCont.close();
					JOptionPane.showMessageDialog(frame, "Se ha borrado el producto");
					Statement muestra = con.createStatement();
					ResultSet visualizar=muestra.executeQuery("SELECT * FROM Productos");
					model_Producto.setRowCount(0);					
					while (visualizar.next()) {
						Object[] row = new Object[5];
						row[0] = visualizar.getString("Cod_Prod");
						row[1] = visualizar.getString("Desc_Prod");
						row[2] = visualizar.getInt("Un_venta");
						row[3] = visualizar.getString("Un_almacen");
						row[4] = visualizar.getString("Un_total");
						model_Producto.addRow(row);
						con.close();
					}
				}catch (SQLException ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage(),"Advertencia",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewBorrar.setBounds(650, 418, 117, 25);
		frame.getContentPane().add(btnNewBorrar);

		JLabel lblID = new JLabel("ID");
		lblID.setBounds(84, 287, 25, 15);
		frame.getContentPane().add(lblID);

		JLabel lblDescripcion = new JLabel("Descripcion");
		lblDescripcion.setBounds(29, 329, 96, 15);
		frame.getContentPane().add(lblDescripcion);

		JLabel lblUnVenta = new JLabel("En venta");
		lblUnVenta.setBounds(272, 287, 70, 15);
		frame.getContentPane().add(lblUnVenta);

		JLabel lblEnAlmacen = new JLabel("Almacen");
		lblEnAlmacen.setBounds(272, 329, 70, 15);
		frame.getContentPane().add(lblEnAlmacen);

		JLabel lblTotal = new JLabel("Total");
		lblTotal.setBounds(540, 287, 41, 15);
		frame.getContentPane().add(lblTotal);
		lblTotal.setVisible(false);

		textField_ID = new JTextField();
		textField_ID.setBounds(127, 285, 114, 19);
		frame.getContentPane().add(textField_ID);
		textField_ID.setColumns(10);

		textField_Desc = new JTextField();
		textField_Desc.setBounds(127, 327, 114, 19);
		frame.getContentPane().add(textField_Desc);
		textField_Desc.setColumns(10);

		textField_Venta = new JTextField();
		textField_Venta.setBounds(360, 285, 114, 19);
		frame.getContentPane().add(textField_Venta);
		textField_Venta.setColumns(10);

		textField_Almacen = new JTextField();
		textField_Almacen.setBounds(360, 327, 114, 19);
		frame.getContentPane().add(textField_Almacen);
		textField_Almacen.setColumns(10);

		textField_Total = new JTextField();
		textField_Total.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		textField_Total.setVisible(false);
		textField_Total.setBounds(584, 285, 114, 19);
		frame.getContentPane().add(textField_Total);
		textField_Total.setColumns(10);

	}

}
