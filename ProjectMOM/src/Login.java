import java.awt.*;
import javax.swing.*;
import java.io.IOException;

public class Login extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JButton jb_login;
    private JLabel jl_ip, jl_port, jl_title, jl_nickname;
    private JTextField jt_ip, jt_port, jt_nickname;

    public Login() throws IOException {
        super("login");
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }

    private void initComponents() {
        jb_login = new JButton("Entrar");
        jl_ip = new JLabel("Ip", SwingConstants.CENTER);
        jl_port = new JLabel("Porta", SwingConstants.CENTER);
        jl_title = new JLabel("Mensageria", SwingConstants.CENTER);
        jl_nickname = new JLabel("Apelido", SwingConstants.CENTER);
        jt_ip = new JTextField("127.0.0.1");
        jt_port = new JTextField("4444");
        jt_nickname = new JTextField("");

        jl_title.setBounds(10, 10, 375, 100);
        jl_title.setFont(new Font("Serif", Font.PLAIN, 56));
        jl_title.setForeground(Color.ORANGE);

        jb_login.setBounds(10, 270, 375, 40);

        jl_ip.setBounds(10, 120, 100, 40);
        jl_ip.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        jl_ip.setForeground(Color.ORANGE);

        jl_port.setBounds(10, 170, 100, 40);
        jl_port.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        jl_port.setForeground(Color.ORANGE);
        
        jl_nickname.setBounds(10, 220, 100, 40);
        jl_nickname.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        jl_nickname.setForeground(Color.ORANGE);

        jt_ip.setBounds(120, 120, 265, 40);
        jt_port.setBounds(120, 170, 265, 40);
        jt_nickname.setBounds(120, 220, 265, 40);
    }

    private void configComponents() {
        this.setLayout(null);
        this.setMinimumSize(new Dimension(410, 360));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.BLACK);
    }

    private void insertComponents() {
        this.add(jb_login);
        this.add(jl_ip);
        this.add(jl_port);
        this.add(jl_title);
        this.add(jl_nickname);
        this.add(jt_ip);
        this.add(jt_port);
        this.add(jt_nickname);
    }

    private void insertActions() throws IOException {
        jb_login.addActionListener(event -> {
            String ip = jt_ip.getText();
            jt_ip.setText("127.0.0.1");
            int port = Integer.parseInt(jt_port.getText());
            jt_port.setText("4444");
            String nickname = jt_nickname.getText();
            jt_nickname.setText("");

			new Home(ip, port, nickname);

            this.dispose();
        });
    }

    private void start(){
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        Login login = new Login();
    }


    
}
