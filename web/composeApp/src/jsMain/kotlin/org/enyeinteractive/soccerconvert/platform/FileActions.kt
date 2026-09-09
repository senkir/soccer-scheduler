package org.enyeinteractive.soccerconvert.platform

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

actual fun downloadTextFile(fileName: String, mimeType: String, content: String) {
    val blob: Blob = Blob(arrayOf(content), BlobPropertyBag(type = mimeType))
    val objectUrl: String = URL.createObjectURL(blob)
    val anchor: HTMLAnchorElement = document.createElement(ANCHOR_TAG) as HTMLAnchorElement
    anchor.href = objectUrl
    anchor.download = fileName
    document.body?.appendChild(anchor)
    anchor.click()
    anchor.remove()
    URL.revokeObjectURL(objectUrl)
}

actual fun openUrl(url: String) {
    window.open(url, NEW_TAB_TARGET)
}

private const val ANCHOR_TAG: String = "a"
private const val NEW_TAB_TARGET: String = "_blank"
