package bletch.tektopiainformation.gui;

import bletch.tektopiainformation.enums.GuiPageType;

public class GuiPage {

	private final GuiPageType guiPageType;
	private final int pageIndex;
	private final String dataKey;
	private final String bookmarkKey;
	
	public GuiPage(GuiPageType guiPageType, int pageIndex) {
		this.guiPageType = guiPageType;
		this.pageIndex = pageIndex;
		this.dataKey = "";
		this.bookmarkKey = "";
	}
	
	public GuiPage(GuiPageType guiPageType, int pageIndex, String dataKey) {
		this.guiPageType = guiPageType;
		this.pageIndex = pageIndex;
		this.dataKey = dataKey != null ? dataKey : "";
		this.bookmarkKey = "";
	}
	
	public GuiPage(GuiPageType guiPageType, int pageIndex, String dataKey, String bookmarkKey) {
		this.guiPageType = guiPageType;
		this.pageIndex = pageIndex;
		this.dataKey = dataKey != null ? dataKey : "";
		this.bookmarkKey = bookmarkKey != null ? bookmarkKey : "";
	}
	
	public GuiPageType getGuiPageType() {
		return this.guiPageType;
	}
	
	public int getPageIndex() {
		return this.pageIndex;
	}
	
	public String getDataKey() {
		return this.dataKey;
	}
	
	public String getBookmarkKey() {
		return this.bookmarkKey;
	}
	
	public boolean isLeftPage() {
		return this.pageIndex % 2 == 0;
	}
	
	public boolean isRightPage() {
		return this.pageIndex % 2 != 0;
	}
	
}
