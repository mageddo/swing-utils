package com.mageddo.swingutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SwingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwingUtil.class);

	/**
	 * Adiciona uma mensagem de erro usada para momentos em que a
	 * ação é requirida para continuar
	 *
	 * @param parent
	 * @param message
	 */
	public static void errorMsg(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Erro encontrado", JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * Adiciona uma mensagem de erro usada para momentos em que a 
	 * ação é requirida para continuar
	 * @param parent
	 * @param message
	 */

	/**
	 * Usada para quando ocorrerem erros inesperados dentro do sistema
	 */
	public static void fatalErrorMsg(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Erro encontrado", JOptionPane.ERROR_MESSAGE);
		LOGGER.error("foi encontrado um erro fatal no sistema: %s", message);
	}

	public static void warnMsg(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Aviso", JOptionPane.WARNING_MESSAGE);
	}

	public static void infoMsg(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
	}


	/**
	 * Abre uma página da web (até então só abre no firefox e no linux)
	 *
	 * @param uri
	 */
	@Deprecated
	private static int openWebpage(String uri) throws IOException, InterruptedException {
//		if (SystemUtils.IS_OS_LINUX) {
//			return new ProcessBuilder(
//				"sh",
//				"-c",
//				"firefox -new-tab \"" + uri + "\""
//			).start().waitFor();
//		} else {
//			throw new IllegalArgumentException("Only linux is supported");
//		}
		return -1;
	}

	/**
	 * Seta um tema no sistema <br>
	 * Depois é necessário utilizar  <b>SwingUtilities.updateComponentTreeUI</b>
	 *
	 * @param themeClass pacote para a classe do tema
	 */
	public static void setTheme(String themeClass) throws Exception {
		try {
			UIManager.setLookAndFeel(themeClass);
		} catch (Exception e) {
			LOGGER.error("não foi possivel setar o tema %s", themeClass, e);
			throw e;
		}
	}


	/**
	 * Gera um buffer de imagem a partir do componente passado
	 *
	 * @param component componente a ser salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 */
	public static BufferedImage componentToImage(Component component) {
		return componentToImage(component, true);
	}

	/**
	 * Gera um buffer de imagem a partir do componente passado
	 *
	 * @param component componente a ser salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 * @param visible   se irá gerar o conteudo que está escondido ou não (por exemplo dentro
	 *                  de um JScrollPane)
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
	public static void saveImage(Component component) throws IOException {
		saveImage(null, componentToImage(component), null);
	}

	public static void saveImage(Component parent, Component component) throws IOException {
		saveImage(parent, componentToImage(component), null);
	}

	/**
	 * @param parent    pai para eventual exibição de modal
	 * @param component componente a ser convertido em imagem e salvo (Caso seja o componente grande colocar o interno e não o scrollpane)
	 */
	public static void saveImage(Component parent, Component component, File target) throws IOException {
		saveImage(parent, componentToImage(component), target);
	}

	/**
	 * @param parent pai para eventual exibição de modal
	 * @param image  imagem a ser salva
	 */
	public static void saveImage(Component parent, BufferedImage image, File target) throws IOException {

		final JFileChooser fileSave = new JFileChooser();
		final javax.swing.filechooser.FileFilter filter = new FileNameExtensionFilter("Arquivos de imagem", "jpg");
		fileSave.setAcceptAllFileFilterUsed(false);
		fileSave.setFileFilter(filter);
		if (target == null)
			target = new File(System.getProperty("user.home") + "/screenshot.png");

		fileSave.setCurrentDirectory(target);
		fileSave.setSelectedFile(target);

		if(fileSave.showDialog(parent, "Save") != JFileChooser.APPROVE_OPTION){
			return ;
		}

		if (!confirmMsg(fileSave)){
			return;
		}

		ImageIO.write(image, "jpg", target);

	}

	private static boolean confirmMsg(JFileChooser fileSave) {
		return JOptionPane.YES_OPTION !=
			JOptionPane.showConfirmDialog(fileSave, "Já existe um arquivo com este nome deseja substituir?", "Confirmação", JOptionPane.YES_NO_OPTION);
	}

	public static boolean confirm(Component parent, Object msg) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, msg, "Confirmação", JOptionPane.YES_NO_OPTION);
	}

}
