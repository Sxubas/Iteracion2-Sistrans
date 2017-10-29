package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.glass.ui.Size;

import oracle.net.aso.p;
import vos.CheckOut;
import vos.EquivalenciasProducto;
import vos.Ingrediente;
import vos.Producto;
import vos.Producto_CheckOut;
import vos.Producto_Menu;
import vos.ProductosBodega;
import vos.Restaurante;
import vos.Restaurante_Producto;
import vosContainers.PedidoMenu;

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

	public boolean esRestaurante(String nombre, String contrasena)throws SQLException, Exception 
	{
		System.out.println(nombre + " // " + contrasena );
		String sql="SELECT * FROM USUARIO U WHERE U.USUARIO LIKE '"+nombre+"' AND U.CONTRASENA LIKE '"+contrasena+"' AND U.ROL LIKE 'admin restaurante'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		return rs.next();
	}
	
	
	public boolean esCliente(String nombre, String contrasena)throws SQLException, Exception
	{
		String sql="SELECT * FROM USUARIO U WHERE U.USUARIO = '"+nombre+"' AND U.CONTRASENA LIKE '"+contrasena+"' AND U.ROL LIKE 'cliente'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		return rs.next();
	}
	public boolean esAcmon(String nombre, String contrase�a)throws SQLException, Exception
	{
		String sql="SELECT * FROM USUARIO U WHERE U.USUARIO = '"+nombre+"'"+" AND U.CONTRASE�A = '"+contrase�a+"'"+" AND U.ROL='admin DB'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		return rs.next();
	}
	
	
	// RF 11 - Equivalencias ingrediente
	public void registrarEquivalenciaDeIngrediente(String ing1, String ing2, String restaurante, String usuario, String contr)throws SQLException, Exception 
	{
		if(esRestaurante(usuario, contr))
		{
			int eq1 = -1;
			int eq2 = -1;
			String sql = "";
			
			sql = "SELECT EQUIVALENCIA FROM EQUIVALENCIASINGREDIENTE WHERE INGREDIENTE_NOMBRE LIKE '" + ing1 +"' AND RESTAURANTE_NOMBRE LIKE '" + restaurante + "'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq1 = rs.getInt(1);
			}
			
			sql = "SELECT EQUIVALENCIA FROM EQUIVALENCIASINGREDIENTE WHERE INGREDIENTE_NOMBRE = '" + ing2 +"' AND RESTAURANTE_NOMBRE = '" + restaurante + "'";
			prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq2 = rs.getInt(1);
			}
			
			if(eq1 >0 && eq2>0)
			{
				throw new Exception("Esos productos ya pertenecen a alguna equivalencia del restaurante");
			}
			
			
			if(eq1 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASINGREDIENTE VALUES ('" + restaurante + "', '" + ing2 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq1);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
			}
			else if (eq2 > 0)
			{
				sql = "INSERT INTO EQUIVALENCIASINGREDIENTE VALUES ('" + restaurante + "', '" + ing1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, eq2);
				recursos.add(prepStmt);
				prepStmt.execute();
			}
			else
			{
				int max;
				sql = "SELECT MAX(E.EQUIVALENCIA) FROM EQUIVALENCIASINGREDIENTE E WHERE E.RESTAURANTE_NOMBRE = '" + restaurante + "'";
				prepStmt = conn.prepareStatement(sql);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
				rs.next();
				max = rs.getInt(1);
				
				sql = "INSERT INTO EQUIVALENCIASINGREDIENTE VALUES ('" + restaurante + "', '" + ing1 + "', ?)";
				prepStmt = conn.prepareStatement(sql);
				prepStmt.setInt(1, max+1);
				recursos.add(prepStmt);
				prepStmt.execute();
				
				sql = "INSERT INTO EQUIVALENCIASINGREDIENTE VALUES ('" + restaurante + "', '" + ing2 + "', ?)";
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

	
	// RF 12 - Equivalencias producto
	public void registrarEquivalenciaDeProducto(String prod1, String prod2, String restaurante, String usuario, String contr)throws SQLException, Exception 
	{
		if(esRestaurante(usuario, contr))
		{
			int eq1 = -1;
			int eq2 = -1;
			String sql = "";
			
			sql = "SELECT E.EQUIVALENCIA FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + prod1 +"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq1 = rs.getInt(1);
			}
			
			sql = "SELECT E.EQUIVALENCIA FROM EQUIVALENCIASPRODUCTO E WHERE E.PRODUCTO_NOMBRE = '" + prod2 +"'";
			prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			rs = prepStmt.executeQuery();
			
			if(rs.next())
			{
				eq2 = rs.getInt(1);
			}
			
			if(eq1 >0 && eq2>0)
			{
				throw new Exception("Esos productos ya pertenecen a alguna equivalencia del restaurante");
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
	
	
	//RF 13 - Surtir restaurante
	public void surtirRestaurante(String rest) throws SQLException, Exception
	{
		ArrayList<ProductosBodega> productos=darProductosRestaurante(rest);
		String sql="";
		for (int i = 0; i < productos.size(); i++)
		{
			sql = "UPDATE PRODUCTOSBODEGA P SET CANTIDADPRODUCTO="+productos.get(i).getMaximo() + " WHERE P.PRODUCTO_NOMBRE LIKE '"+productos.get(i).getNombreProd()+"' AND P.INVENTARIO_RESTAURANTE_NOMBRE LIKE '"+productos.get(i).getNombreRest()+"'";			
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			prepStmt.executeQuery();
		}
	}
	
	public void retirarproductosbodega(String rest, List<Producto> prods)throws SQLException, Exception
	{
		String sql="";
		for (int i = 0; i < prods.size(); i++)
		{
			int cantActual=cantidadEnBodega(prods.get(i).getNombre(), rest);
			if(cantActual==0)
			{
				throw new Exception("no se puede vender el producto, no hay mas en bodega.");
			}
			else
			{
				cantActual--;
			}
			 sql+= "UPDATE PRODUCTOSBODEGA P SET CANTIDADPRODUCTO="+cantActual;
			 sql+=" WHERE P.PRODUCTO_NOMBRE LIKE '"+prods.get(i).getNombre()+"' AND P.INVENTARIO_RESTAURANTE_NOMBRE LIKE '"+rest+"'";			
		}
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}
	
	public ArrayList<ProductosBodega> darProductosRestaurante(String rest)throws SQLException, Exception
	{
		ArrayList<ProductosBodega> productos = new ArrayList<ProductosBodega>();
		String sql = "SELECT * FROM PRODUCTOSBODEGA P WHERE P.INVENTARIO_RESTAURANTE_NOMBRE LIKE "+ "'"+rest+"'";
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
	public List<Producto> buscarProductosPorNombre(List<String> name) throws SQLException, Exception {
		List<Producto> productos = new ArrayList<Producto>();
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
	
	public void registrarpedidoIter3REQ14(String nombrePM, boolean esMenu, List<String> productos,String usuario, String contr,String restaurante)throws SQLException, Exception 
	{
		String cliente="NULL";
		if(usuario!=null)
		{
			if(!esCliente(usuario, contr))
			{
				throw new Exception("el usuario o la contrase�a no existen o son incorrectos.");
			}
			cliente=usuario;
		}
		int max;
		String sql = "SELECT MAX(C.CHECKOUT_ID) FROM PRODUCTO_CHECKOUT C";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		rs.next();
		max = rs.getInt(1);
		if(esMenu) 
		{
			ArrayList<CheckOut> cks=new ArrayList<>();
			//lista productos que llegan por parametro en el pedido.
			List<Producto> productosB=buscarProductosPorNombre(productos);
			//lista productos del men�.
			List<Producto> productosA=darProductosMenu(nombrePM);
			
			if(!diferentesCategorias(productosB))
			{
				throw new Exception("No puede haber en un Men� productos de la misma categor�a.");
			}
			//equivalencias productos del pedido.
			ArrayList<Integer> equivalenciasB=darEquivalenciasProductos(productos, restaurante);
			//equivalencias productos del men�.
			ArrayList<Integer> equivalenciasA=darEquivalenciasProductosMen�(productosA, restaurante);
			for (int i = 0; i < equivalenciasA.size(); i++) 
			{
				Producto poductoA=productosA.get(i);
				int equivalenciaA=equivalenciasA.get(i);
				for(int j=0;j< equivalenciasB.size();j++)
				{
					Producto poductoB=productosB.get(j);
					int equivalenciaB=equivalenciasB.get(j);
					if(poductoA.getCategoria()==poductoB.getCategoria()&&equivalenciaA!=equivalenciaB)
					{
						throw new Exception("Los algunos de los productos pedidos no son equivalentes a los permitidos en el menu, por ejemplo el producto: "+poductoB.getNombre()+".");
					}
				}
				
			}

			for (int i = 0; i < productosB.size(); i++)
			{
				String time=String.valueOf(System.currentTimeMillis());   

				String sql2= "INSERT INTO CHECKOUT VALUES ("+max+", "+0+", "+time+", NULL, "+cliente+")";
				sql2+="INSERT INTO PRODUCTO_CHECKOUT VALUES ("+max+", "+productosB.get(i).getNombre()+", "+restaurante+","+1+","+nombrePM+")";

				prepStmt = conn.prepareStatement(sql2);
				recursos.add(prepStmt);
				rs = prepStmt.executeQuery();
			}
		}

		if(!esMenu)
		{
			String time=String.valueOf(System.currentTimeMillis());   
			String sql2= "INSERT INTO CHECKOUT VALUES ("+max+", "+0+", "+time+", NULL, "+cliente+")";
			sql2+="INSERT INTO PRODUCTO_CHECKOUT VALUES ("+max+", "+nombrePM+", "+restaurante+","+1+",NULL)";
			prepStmt = conn.prepareStatement(sql2);
			recursos.add(prepStmt);
			prepStmt.execute();
		}
	}
	public void registrarpedidoMesa(List<PedidoMenu> pedidos)throws SQLException, Exception
	{
		
		Iterator<PedidoMenu> iter=pedidos.iterator();
		while(iter.hasNext())
		{
			PedidoMenu temp=iter.next();
			String nombrePM=temp.getNombre();
			boolean esMenu=temp.getEsMenu();
			List<String> productos=temp.getAlternativos();
			String usuario=temp.getUsuario();
			String contr=temp.getPass();
			String restaurante=temp.getRestaurante();
			if(esMenu)
			{
				for(String prod:productos)
				{
					int c=cantidadEnBodega(prod, restaurante);
					if(c==0)
					{
						throw new Exception("No se puede registrar el pedido ya que no esta disponible el producto: "+prod);
					}
				}
			}
			else
			{
				int c=cantidadEnBodega(nombrePM, restaurante);
				if(c==0)
				{
					throw new Exception("No se puede registrar el pedido ya que no esta disponible el producto: "+nombrePM);
				}
			}
				
		}
		while (iter.hasNext()) 
		{
			PedidoMenu temp=iter.next();
			String nombrePM=temp.getNombre();
			boolean esMenu=temp.getEsMenu();
			List<String> productos=temp.getAlternativos();
			String usuario=temp.getUsuario();
			String contr=temp.getPass();
			String restaurante=temp.getRestaurante();
			registrarpedidoIter3REQ14(nombrePM, esMenu, productos, usuario, contr, restaurante);
		}
	}
	public void registrarServicioMesaIter3REQ16(int idCheckOut)throws SQLException, Exception
	{
		String sql = "SELECT * FROM PRODUCTO_CHECKOUT C WHERE C.CHECKOUT_ID="+idCheckOut;
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) 
		{
			String prodNom=rs.getString("PRODUCTO_NOMBRE");
			String restaurante=rs.getString("RESTAURANTE_NOMBRE");
			List<String> names=new ArrayList<>();
			names.add(prodNom);
			List<Producto> prods=buscarProductosPorNombre(names);
			retirarproductosbodega(restaurante, prods);
			sql="UPDATE CHECKOUT C SET ENTREGADO=1 WHERE C.ID="+idCheckOut;
			PreparedStatement prepStmt2 = conn.prepareStatement(sql);
			recursos.add(prepStmt2);
			prepStmt2.executeQuery();	
		}		

	}
	public void cancelarPedido(int idCheckOut)throws SQLException, Exception
	{
		
	}
	public List<Producto> consumoClienteRegistrado(String usuario, String contrase�a, String usuarioBusqueda)throws SQLException, Exception
	{
		List<Producto> respuesta=new ArrayList<>();
			if(esCliente(usuario, contrase�a))
			{
				if(usuarioBusqueda!=null)
				{
					throw new Exception("Usted no tiene acceso a la informaci�n de otros usuarios.");
				}
				respuesta=buscarProductosPorNombre(darNombresProductosCliente(usuario));
			}
			else if(esAcmon(usuario, contrase�a)) 
			{
				respuesta=buscarProductosPorNombre(darNombresProductosCliente(usuarioBusqueda));
			}
			else
			{
				throw new Exception("El usuario solicitado no existe.");
			}
		return respuesta;
	}
	public List<String> darNombresProductosCliente(String usuario) throws SQLException, Exception
	{
		String sql="SELECT * FROM CHECKOUT C WHERE C.CLISREGS_USUARIO_CLIENTE='"+usuario+"'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		List<Integer>idsck=new ArrayList<>();
		while(rs.next())
		{
			int a=rs.getInt("ID");
			idsck.add(a);
		}
		Iterator<Integer>iter=idsck.iterator();
		List<String>prods=new ArrayList<>();
		while(iter.hasNext())
		{
			String sql2="SELECT * FROM PRODUCTO_CHECKOUT C WHERE C.CHECKOUT_ID="+iter.next();
			PreparedStatement prepStmt2 = conn.prepareStatement(sql2);
			recursos.add(prepStmt2);
			ResultSet rs2 = prepStmt.executeQuery();
			while(rs.next())
			{
				String b=rs2.getString("PRODUCTO_NOMBRE");
				prods.add(b);
			}
		}
		return prods;
	}
	
	public boolean diferentesCategorias(List<Producto> prods)
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
	public List<Producto> darProductosMenu(String menu)throws SQLException, Exception
	{
		List<String> prods=new ArrayList<>() ;
		String sql = "SELECT * FROM PRODUCTO_MENU WHERE MENU_NOMBRE ='" + menu + "'";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) 
		{
			String nombre_producto = rs.getString("PRODUCTO_NOMBRE");
			prods.add(nombre_producto);
		}
		return buscarProductosPorNombre(prods);
	}
	public ArrayList<Integer> darEquivalenciasProductos(List<String> productos, String restaurante)throws SQLException, Exception
	{
		ArrayList<Integer> eqs=new ArrayList<>() ;
		Iterator<String> p=productos.iterator();
		while(p.hasNext())
		{
			int equivalencia=0;
			//sacar equivalencia del producto
			String sql = "SELECT * FROM EQUIVALECIASPRODUCTO WHERE PRODUCTO_NOMBRE ='" + p.next() + "' AND RESTAURANTE_NOMBRE='"+restaurante+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			equivalencia = rs.getInt("EQUIVALENCIA");
			eqs.add(equivalencia);
		}
		return eqs;
	}
	public ArrayList<Integer> darEquivalenciasProductosMen�(List<Producto> productos, String restaurante)throws SQLException, Exception
	{
		ArrayList<Integer> eqs=new ArrayList<>() ;
		Iterator<Producto> p=productos.iterator();
		while(p.hasNext())
		{
			int equivalencia=0;
			//sacar equivalencia del producto
			String sql = "SELECT * FROM EQUIVALECIASPRODUCTO WHERE PRODUCTO_NOMBRE ='" + p.next().getNombre() + "' AND RESTAURANTE_NOMBRE='"+restaurante+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			recursos.add(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			equivalencia = rs.getInt("EQUIVALENCIA");
			eqs.add(equivalencia);
		}
		return eqs;
	} 	
	public Integer cantidadEnBodega(String prod, String rest)throws SQLException, Exception
	{
		Integer a=0;
		String sql = "SELECT * FROM PRODUCTOSBODEGA P WHERE P.NOMBRE LIKE '"+ prod+"' "
				+ "AND P.INVENTARIO_RESTAURANTE_NOMBRE LIKE '"+rest+"'";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			a = rs.getInt("CANTIDADPRODUCTO");
		}
		return a;
	}
	
	
}
