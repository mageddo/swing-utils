package com.mageddo.swingutils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.CellRendererPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

public class SwingUtil {
	
	private static final Logger log = Logger.getLogger(SwingUtil.class);
	private static File ULTIMA_PASTA;
	
	/**
	 * Adiciona uma mensagem de erro usada para momentos em que a 
	 * ação é requirida para continuar
	 * @param parent
	 * @param message
	 */
	public static void mensagemErro(Component parent, String message){
		JOptionPane.showMessageDialog(parent, message, "Erro encontrado", JOptionPane.ERROR_MESSAGE) ;
	}
	/**
	 * Adiciona uma mensagem de erro usada para momentos em que a 
	 * ação é requirida para continuar
	 * @param parent
	 * @param message
	 */
	
	/**
	 * Usada para quando ocorrerem erros inesperados dentro do sistema 
	 * @param parent
	 * @param message
	 */
	public static void mensagemErroFatal(Component parent, String message){
		JOptionPane.showMessageDialog(parent, message, "Erro encontrado", JOptionPane.ERROR_MESSAGE) ;
		log .error("foi encontrado um erro fatal no sistema: %s", message);
	}
	
	public static void mensagemAviso(Component parent, String message){
		JOptionPane.showMessageDialog(parent, message, "Aviso", JOptionPane.WARNING_MESSAGE) ;
	}
	
	public static void mensagemInformacao(Component parent, String message){
		JOptionPane.showMessageDialog(parent, message, "Informação", JOptionPane.INFORMATION_MESSAGE) ;
	}
	


	/**
	 * Abre uma página da web (até então só abre no firefox e no linux)
	 * @param uri
	 */
	public static void openWebpage(String uri) {
			if(SystemUtils.IS_OS_LINUX) {
			try {
				new ProcessBuilder(
					"sh",
					"-c",
					"firefox -new-tab \"" + uri +"\""
				).start();
				return ;
			} catch (IOException e) {}
			}else{
				mensagemErroFatal(null, "Desculpe, não foi possível abrir o link requisitado");	    		
			}
	}
	
	/**
	 * Seta um tema no sistema <br>
	 * Depois é necessário utilizar  <b>SwingUtilities.updateComponentTreeUI</b>
	 * @param themeClass pacote para a classe do tema
	 */
	public static void setTheme(String themeClass){
		try {
						UIManager.setLookAndFeel(themeClass);
		} catch (Exception e) {
			log.error("não foi possivel setar o tema %s", themeClass, e);
		}
	}
	
	
	/**
	 * Gera um buffer de imagem a partir do componente passado 
	 * @param component componente a ser salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 */
	public static BufferedImage componentToImage(Component component) {
		return componentToImage(component, true);
	}
	
	/**
	 * Gera um buffer de imagem a partir do componente passado 
	 * @param component componente a ser salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 * @param visible se irá gerar o conteudo que está escondido ou não (por exemplo dentro 
	 * de um JScrollPane)
	 * @return
	 */
	public static BufferedImage componentToImage(Component component, boolean visible) {
			if (visible) {
					BufferedImage img = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TRANSLUCENT);
					Graphics2D g2d = (Graphics2D) img.getGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					component.paintAll(g2d);
					return img;
			} else {
					component.setSize(component.getPreferredSize());
					layoutComponent(component);
					BufferedImage img = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TRANSLUCENT);
					CellRendererPane crp = new CellRendererPane();
					crp.add(component);
					crp.paintComponent(img.createGraphics(), component, crp, component.getBounds());
					return img;
			}
	}
	
	private static void layoutComponent(Component c) {
			synchronized (c.getTreeLock()) {
					c.doLayout();
					if (c instanceof Container) {
							for (Component child : ((Container) c).getComponents()) {
									layoutComponent(child);
							}
					}
			}
	}
	/**
	 * @param component componente a ser salvo (Caso seja o componente grande colocar o interno e não o scrollpane) 
	 */
	public static void saveImage(Component component){
		saveImage(null, componentToImage(component));
	}

	/**
	 * @param parent pai para eventual exibição de modal
	 * @param component componente a ser convertido em imagem e salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 */
	public static void saveImage(Component parent, Component component){
		saveImage(parent, componentToImage(component));
	}
	
	
	
	/**
	 * @param parent pai para eventual exibição de modal
	 * @param image imagem a ser salva 
	 */
	public static void saveImage(Component parent, BufferedImage image){
		try {
			
			final JFileChooser fileSave = new JFileChooser();
			final javax.swing.filechooser.FileFilter filter = new FileNameExtensionFilter("Arquivos de imagem", "png");
			fileSave.setAcceptAllFileFilterUsed(false);
			fileSave.setFileFilter(filter);
			if(ULTIMA_PASTA == null)
				ULTIMA_PASTA = new File(System.getProperty("user.home") + "/screenshot.png");
			
			fileSave.setCurrentDirectory(ULTIMA_PASTA);
			fileSave.setSelectedFile(ULTIMA_PASTA);

			final int ret = fileSave.showDialog(parent, "Save");
			if (ret == JFileChooser.APPROVE_OPTION) {
				ULTIMA_PASTA = fileSave.getSelectedFile();
				if(ULTIMA_PASTA.exists())
					if(
						JOptionPane.YES_OPTION != 
						JOptionPane.showConfirmDialog(fileSave, "Já existe um arquivo com este nome deseja substituir?", "Confirmação", JOptionPane.YES_NO_OPTION)
					)
						return;
				
					ImageIO.write(image, "png", ULTIMA_PASTA);
					
			}
			
		} catch (Exception e) {
			SwingUtil.mensagemErro(parent, "Erro ao salvar a imagem");
		}
		}
	
	public static boolean confirm(Component parent, Object msg){
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, msg, "Confirmação", JOptionPane.YES_NO_OPTION);
	}
	
}
