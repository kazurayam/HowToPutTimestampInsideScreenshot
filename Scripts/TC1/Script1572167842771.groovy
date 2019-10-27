import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import javax.imageio.ImageIO

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.openBrowser('')
WebUI.setViewPortSize(800, 600)
WebUI.navigateToUrl('https://forum.katalon.com/t/how-to-put-time-stamp-in-screenshots/8831/3')
WebUI.waitForPageLoad(10)
WebUI.delay(1)

// get the current timestamp
LocalDateTime now = LocalDateTime.now()
DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd_HH-mm-ss')

Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path tmpDir = projectDir.resolve('tmp')
Files.createDirectories(tmpDir)
Path screenshot = tmpDir.resolve("${now.format(formatter)}.png")

// take screenshot
WebUI.takeScreenshot(screenshot.toString(), FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()

// method to put text onto the original image
BufferedImage modifyImage(BufferedImage original, String text) {
	int w = original.getWidth()
	int h = original.getHeight()
	BufferedImage newImage = new BufferedImage(w, h, original.getType())
	Graphics2D g2d = newImage.createGraphics()
	g2d.drawImage(original, 0, 0, w, h, null)
	g2d.setPaint(Color.RED)
	g2d.setFont(new Font("Serif", Font.BOLD, 20))
	String s = text
	FontMetrics fm = g2d.getFontMetrics()
	int x = newImage.getWidth() - fm.stringWidth(s) - 5
	int y = fm.getHeight()
	g2d.drawString(s, x, y)
	g2d.dispose()
	return newImage
}

// read the screenshot PNG file
BufferedImage bi = ImageIO.read(screenshot.toFile())

// put timestamp on the image; new image will be returned
BufferedImage bi2 = modifyImage(bi, now.format(formatter))

// write the modified image into another filer
Path modified = tmpDir.resolve("${now.format(formatter)}.modified.png")
ImageIO.write(bi2, "png", modified.toFile())
