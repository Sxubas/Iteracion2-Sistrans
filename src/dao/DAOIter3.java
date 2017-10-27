package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vos.Ingrediente;
import vos.Producto;
import vos.ProductosBodega;
import vos.Restaurante;

public class DAOIter3 {

	/**
	 * Arraylits de recursos que se usan para la ejecución de sentencias SQL
	 */
	private ArrayList<Object> recursos;

	/**
	 * Atributo que genera la conexión a la base de datos
	 */
	private Connection conn;

	/**
	 * Metodo constructor que crea DAOProducto
	 * <b>post: </b> Crea la instancia del DAO e inicializa el Arraylist de recursos
	 */
	public DAOIter3() {
		recursos = new ArrayList<Object>();
	}

	/**
	 * Metodo que cierra todos los recursos que estan enel arreglo de recursos
	 * <b>post: </b> Todos los recurso del arreglo de recursos han sido cerrados
	 */
	public void cerrarRecursos() {
		for(Object ob : recursos){
			if(ob instanceof PreparedStatement)
				try {
					((PreparedStatement) ob).close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		}
	}

	/**
	 * Metodo que inicializa la connection del DAO a la base de datos con la conexión que entra como parametro.
	 * @param con  - connection a la base de datos
	 */
	public void setConn(Connection con){
		this.conn = con;
	}

	public boolean esRestaurante(String nombre, String contrase�a)throws SQLException, Exception 
	{
		String sql="SELECT * FROM USUARIO U WHERE";
		sql+="U.USUARIO = '"+nombre+"'"+" AND U.CONTRASE�A = '"+contrase�a+"'"+" AND U.ROL='admin restaurante'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		return rs.next();
	}
	
	
	public boolean esCliente(String nombre, String contrase�a)throws SQLException, Exception
	{
		String sql="SELECT * FROM USUARIO U WHERE";
		sql+="U.USUARIO = '"+nombre+"'"+" AND U.CONTRASE�A = '"+contrase�a+"'"+" AND U.ROL='cliente'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		return rs.next();
	}
	
	
	
	public void registrarEquivalenciaDeProducto(String prod1, String prod2, String restaurante, String usuario, String contr)throws SQLException, Exception 
	{
		if(esRestaurante(usuario, contr))
		{
			int eq1 = -1;
			int eq2 = -1;
			String sql = "";
			
			sql = "SELECT E.EQUIVALENCIAS FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + prod1 +"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq1 = rs.getInt(1);
			}
			
			sql = "SELECT E.EQUIVALENCIAS FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + prod2 +"'";
			prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq2 = rs.getInt(1);
			}
			
			if(eq1 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + prod2 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq1);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
			}
			else if (eq2 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + prod1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq2);
				recursos.add(prepStmt);
				prepStmt.execute();
			}
			else
			{
				int max;
				sql = "SELECT MAX(E.EQUIVALENCIA) FROM EQUIVALENCIASPRODUCTO E WHERE E.RESTAURANTE_NOMBRE = '" + restaurante + "'";
				prepStmt = conn.prepareStatement(sql);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
				rs.next();
				max = rs.getInt(1);
				
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + prod1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, max+1);
				recursos.add(prepStmt);
				prepStmt.execute();
				
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + prod2 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, max+1);
				recursos.add(prepStmt);
				prepStmt.execute();
			}
		}
		else
			throw new Exception("Login inv�lido");
	}
	
	
	public void registrarEquivalenciaDeIngrediente(String ing1, String ing2, String restaurante, String usuario, String contr)throws SQLException, Exception 
	{
		if(esRestaurante(usuario, contr))
		{
			int eq1 = -1;
			int eq2 = -1;
			String sql = "";
			
			sql = "SELECT E.EQUIVALENCIAS FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + ing1 +"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq1 = rs.getInt(1);
			}
			
			sql = "SELECT E.EQUIVALENCIAS FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + ing2 +"'";
			prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq2 = rs.getInt(1);
			}
			
			if(eq1 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + ing2 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq1);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
			}
			else if (eq2 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + ing1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq2);
				recursos.add(prepStmt);
				prepStmt.execute();
			}
			else
			{
				int max;
				sql = "SELECT MAX(E.EQUIVALENCIA) FROM EQUIVALENCIASPRODUCTO E WHERE E.RESTAURANTE_NOMBRE = '" + restaurante + "'";
				prepStmt = conn.prepareStatement(sql);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
				rs.next();
				max = rs.getInt(1);
				
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + ing1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, max+1);
				recursos.add(prepStmt);
				prepStmt.execute();
				
				sql = "INSERT INTO EQUIVALENCIASPRODUCTO VALUES ('" + restaurante + "', '" + ing2 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, max+1);
				recursos.add(prepStmt);
				prepStmt.execute();
			}
			prepStmt.close();
		}
		else
			throw new Exception("Login inv�lido");
	}
	
	
	
	public void surtirRestaurante(String rest) throws SQLException, Exception
	{
		ArrayList<ProductosBodega> productos=darProductosRestaurante(rest);
		String sql="";
		for (int i = 0; i < productos.size(); i++)
		{
			 sql+= "UPDATE PRODUCTOSBODEGA P SET CANTIDADPRODUCTO="+productos.get(i).getMaximo();
			 sql+=" WHERE P.PRODUCTO_NOMBRE LIKE '"+productos.get(i).getNombreProd()+"' AND P.INVENTARIO_RESTAURANTE_NOMBRE LIKE '"+productos.get(i).getNombreRest()+"';";			
		}
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}
	
	
	public ArrayList<ProductosBodega> darProductosRestaurante(String rest)throws SQLException, Exception
	{
		ArrayList<ProductosBodega> productos = new ArrayList<ProductosBodega>();
		String sql = "SELECT * FROM PRODUCTOSBODEGA P";
		sql+="WHERE P.INVENTARIO_RESTAURANTE_NOMBRE LIKE "+ "'"+rest+"'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()) {
			String nombreRest = rs.getString("INVENTARIO_RESTAURANTE_NOMBRE");
			String nombreProd = rs.getString("PRODUCTO_NOMBRE");
			Integer cantProd = rs.getInt("CANTIDADPRODUCTO");
			Integer max = rs.getInt("MAXIMO");
			productos.add(new ProductosBodega(nombreRest, nombreProd, cantProd, max));
		}
		return productos;
	}
	
	
	/**
	 * Metodo que busca el/los videos con el nombre que entra como parametro.
	 * @param name - Nombre de el/los videos a buscar
	 * @return ArrayList con los videos encontrados
	 * @throws SQLException - Cualquier error que la base de datos arroje.
	 * @throws Exception - Cualquier error que no corresponda a la base de datos
	 */
	public ArrayList<Producto> buscarProductosPorNombre(List<String> name) throws SQLException, Exception {
		ArrayList<Producto> productos = new ArrayList<Producto>();
		Iterator<String> iter= name.iterator();
		while(iter.hasNext())
		{
			String sql = "SELECT * FROM PRODUCTO WHERE NOMBRE ='" + iter.next() + "'";

			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) 
			{
				String nombre = rs.getString("NOMBRE");
				Integer categoria = rs.getInt("CATEGORIA");
				Integer precioVenta = rs.getInt("PRECIOVENTA");
				Integer costosProduccion = rs.getInt("COSTOPRODUCCION");
				String tipoComidaProd = rs.getString("TIPOCOMIDAPROD");
				Integer tiempoDePreparacion = rs.getInt("TIEMPOPREPARACION");
				productos.add(new Producto(nombre, categoria, precioVenta, costosProduccion, tipoComidaProd,tiempoDePreparacion));
			}
		}

		return productos;
	}
	
	public void registrarpedidoIter3REQ14(String nombrePM, boolean esMenu, List<String> productos,String usuario, String contr)throws SQLException, Exception 
	{
		if(usuario!=null)
		{
			if(!esCliente(usuario, contr))
			{
				throw new Exception("el usuario o la contrase�a no existen o son incorrectos.");
			}
		}
		if(esMenu) 
		{
			ArrayList<Producto> productos2=buscarProductosPorNombre(productos);
			if(!diferentesCategorias(productos2))
			{
				throw new Exception("No puede haber en un Men� productos de la misma categor�a.");
			}
			
			
		}
	}
	
	public boolean diferentesCategorias(ArrayList<Producto> prods)
	{
		boolean hay=false;
		for (int i = 0; i < prods.size()&&!hay; i++) 
		{
			int cat=prods.get(i).getCategoria();
			for (int j = i+1; j < prods.size()&&!hay; j++) 
			{
				if(cat==prods.get(j).getCategoria())
				hay=true;
			}
		}
		return hay;
	}
}
