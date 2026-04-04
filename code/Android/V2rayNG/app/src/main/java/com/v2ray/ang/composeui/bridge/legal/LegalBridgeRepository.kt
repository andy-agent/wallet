package com.v2ray.ang.composeui.bridge.legal

class LegalBridgeRepository {
    fun listDocuments(): List<LegalDocEntry> = LegalDocumentProvider.list()

    fun getDocument(documentId: String): LegalDocEntry? = LegalDocumentProvider.get(documentId)
}
