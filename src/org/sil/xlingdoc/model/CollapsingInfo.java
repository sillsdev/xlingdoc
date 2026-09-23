/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.model;

/**
 * @param beginCollapsed = whether the item is to begin in a collapsed state
 * @param localizationKey = the key on the properties file
 * @param includeElementInSummary = an element within the parent element that should be included in the summary element rather than in the details element
 * @param attributeOverride = the attribute to use for the text content of the summary element
 * 
 * The attributeOverride is to be used instead of the localizationKey
 */
public record CollapsingInfo(boolean beginCollapsed, String localizationKey, String includeElementInSummary, String attributeOverride) {

}
