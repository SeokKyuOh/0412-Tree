/*
 	java에는 자체적으로 MP3를 재생 및 분석하는 기능이 없다.
 	때문에 오픈소스 진영인 apache 에서 제공하는 PIKA라는 기능을 끌어와서 사용하자.
 */

package tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;



public class AppMain extends JFrame implements TreeSelectionListener {
	JPanel p_west, p_center;
	JTree tree;
	JScrollPane scroll;
	DefaultMutableTreeNode root = null;
	JTextArea area;
	String path = "C:/java_workspace2/0412TreeProject/data/";
	String fileLocation;
	Thread thread;
	boolean flag = true;
	
	public AppMain() {
		p_west = new JPanel();
		p_center = new JPanel();

		// createNode();
		// createDirectory();
		createMusicDir();

		tree = new JTree(root);
		scroll = new JScrollPane(tree);
		area = new JTextArea();

		p_west.setPreferredSize(new Dimension(150, 500));
		p_west.setLayout(new BorderLayout()); // borderLayout은 뭐든지 붙는 것을 크게
		p_west.add(scroll);
		add(p_west, BorderLayout.WEST);
		add(area);

		// tree와 리스너 연결
		tree.addTreeSelectionListener(this);

		setSize(700, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void createNode() {
		// 최상위 노드 생성하기
		root = new DefaultMutableTreeNode("과일");

		DefaultMutableTreeNode node1 = null;
		DefaultMutableTreeNode node2 = null;
		DefaultMutableTreeNode node3 = null;

		DefaultMutableTreeNode out1 = null;
		DefaultMutableTreeNode out2 = null;
		DefaultMutableTreeNode out3 = null;

		node1 = new DefaultMutableTreeNode("블루베리");
		node2 = new DefaultMutableTreeNode("레몬");
		node3 = new DefaultMutableTreeNode("수입산과일");

		out1 = new DefaultMutableTreeNode("망고");
		out2 = new DefaultMutableTreeNode("키위");
		out3 = new DefaultMutableTreeNode("바나나");

		node3.add(out1);
		node3.add(out2);
		node3.add(out3);

		root.add(node1);
		root.add(node2);
		root.add(node3);

	}

	// 윈도우의 구조를 보여주기(파일 탐색기)
	public void createDirectory() {
		root = new DefaultMutableTreeNode("내컴퓨터");

		File[] drive = File.listRoots();

		// 디스크 볼륨 정보를 표현해준다
		FileSystemView fsv = FileSystemView.getFileSystemView(); // 추상클래스이지만
																	// 메서드를 살펴보면
																	// 자식을 불러오지
																	// 않아도 생성할 수
																	// 있는 방법이 있는
																	// 경우가 있다.

		for (int i = 0; i < drive.length; i++) {
			DefaultMutableTreeNode node = null;
			String volume = fsv.getSystemDisplayName(drive[i]);

			node = new DefaultMutableTreeNode(volume);
			root.add(node);
		}
	}

	// 음악찾고 재생하기
	public void createMusicDir() {
		root = new DefaultMutableTreeNode("주크박스");

		File file = new File(path);
		File[] child = file.listFiles();

		for (int i = 0; i < child.length; i++) {
			DefaultMutableTreeNode node = null;
			node = new DefaultMutableTreeNode(child[i].getName());
			root.add(node);

		}

	}

	// 선택한 node의 파일에 대한 정보 추출하기
	public void extract(String filename) {
		//System.out.println(filename);

		fileLocation = path+filename;

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		
		try {
			inputstream = new FileInputStream(new File(fileLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ParseContext pcontext = new ParseContext();
		// Mp3 parser
		Mp3Parser Mp3Parser = new Mp3Parser();
		LyricsHandler lyrics;
		
		try {
			Mp3Parser.parse(inputstream, handler, metadata, pcontext);
			lyrics = new LyricsHandler(inputstream, handler);
			while (lyrics.hasLyrics()) {
				area.append(lyrics.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

		area.append("Contents of the document:" + handler.toString());
		area.append("Metadata of the document:");
		
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
			area.append(name + ": " + metadata.get(name));
		}
		
	}

	//선택한 MP3 파일 재생, JLayer
	public void play(){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileLocation));
			AdvancedPlayer player = new AdvancedPlayer(fis);
			if(flag==true){
				player.play();
				flag = false;
			} else if(flag == false){
				player.stop();
				player.play();
				flag = true;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) { // 이벤트를 발생시키는 주체는 tree
		// System.out.println("누름?");
		Object obj = e.getSource();
		JTree tree = (JTree) obj; // 오브젝트형으로는 할 수 있는게 많이 없기 때문에 JTree형으로 바꿔준다.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); // 형변환
		// System.out.println(node.getUserObject()); //sysout은 오브젝트형을 찍을 순 없다.
		// 하지만 오브젝트 내에 출력시 String으로 바꿔주는 기능이 숨어 있기 때문에 출력가능
		extract(node.getUserObject().toString()); // 스트링형으로 변환.
		
		thread = new Thread(){
			public void run(){
				play();
			}
		};
		thread.start();
		
	}
	
	
	

	public static void main(String[] args) {
		new AppMain();
	}

}
