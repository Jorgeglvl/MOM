import javax.swing.JOptionPane;

public class Notificacao {
	
	public static String configuraHost() {
		
		String host;
		
		host = JOptionPane.showInputDialog("Qual o ip do servidor? (Padrão: localhost)");
		if(host==null||host.contentEquals("")) {
			return "127.0.0.1";
		}
		else {
			return host;
		}
	}
	
	public static int configuraPorta() {
		
		String porta;
		
		porta = JOptionPane.showInputDialog("Qual a porta do servidor? (Padrão: 9090)");
		if(porta==null||porta.contentEquals("")) {
			return 9090;
		}
		else {
			return Integer.parseInt(porta);
		}
	}

	public static String configuraNome() {
		
		String nomeFila = null;
		
		nomeFila = JOptionPane.showInputDialog(null,"Digite um nome de usuário","Nome de usuario",1);
		
		while(nomeFila!=null&&nomeFila.contentEquals("")) {
			nomeFila = JOptionPane.showInputDialog(null,"Nome inválido, digite um nome de usuário","Nome de usuario",2);
		}
		
		return nomeFila;
	}
	
	public static String addTopico() {
		
		String nomeTopico = null;
		
		nomeTopico = JOptionPane.showInputDialog("Digite um nome para o Topico");
		
		while(nomeTopico!=null&&nomeTopico.contentEquals("")) {
			nomeTopico = JOptionPane.showInputDialog("Nome inválido, digite um nome para o Topico");
		}
		
		return nomeTopico;
	}
	
	
	public static void usuarioExiste(String nome) {
		JOptionPane.showMessageDialog(null, "Usuario '"+nome+"' ja existe");
	}
	
	public static void topicoExiste(String nome) {
		JOptionPane.showMessageDialog(null, "Topico '"+nome+"' ja existe");
	}
	
	public static void desconectado() {
		JOptionPane.showMessageDialog(null, "Você foi desconectado");
	}
	
	public static void naoExisteUsuario() {
		JOptionPane.showMessageDialog(null, "Usuario nao existe");
	}
	
	public static void naoExisteTopico() {
		JOptionPane.showMessageDialog(null, "Topico nao existe");
	}
	
	public static void servidorNaoDisponivel() {
		JOptionPane.showMessageDialog(null, "Servidor está offline acesse novamente mais tarde");
	}
	
	public static void topicoJaAssinado() {
		JOptionPane.showMessageDialog(null, "Topico ja foi assinado");
	}
	
	public static int confirmaApagarFila() {
		return JOptionPane.showConfirmDialog(null, "Tem certeza?","Apagar Fila",JOptionPane.YES_NO_OPTION);
	}
	
	public static int confirmaApagarTopico() {
		return JOptionPane.showConfirmDialog(null, "Tem certeza?","Apagar Topico",JOptionPane.YES_NO_OPTION);
	}
}
