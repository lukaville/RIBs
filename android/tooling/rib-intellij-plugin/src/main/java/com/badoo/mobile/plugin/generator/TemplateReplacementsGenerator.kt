package com.badoo.mobile.plugin.generator

import com.badoo.mobile.plugin.template.Token
import com.badoo.mobile.plugin.util.addTokenReplacements
import java.lang.IllegalArgumentException

fun createReplacements(templatePackage: String,
                       targetPackage: String,
                       templateModulePackage: String,
                       targetModulePackage: String,
                       tokens: Map<String, Token>,
                       tokenValues: Map<String, String>): Replacements {

    val replacements = Replacements().apply {
        add("$templateModulePackage.R", "$targetModulePackage.R")
        add(templatePackage, targetPackage)
    }

    tokenValues.forEach { tokenId, tokenValue ->
        val token = tokens[tokenId] ?: throw IllegalArgumentException("Token with id $tokenId not found")
        replacements.addTokenReplacements(token, tokenValue)
    }

    return replacements
}
