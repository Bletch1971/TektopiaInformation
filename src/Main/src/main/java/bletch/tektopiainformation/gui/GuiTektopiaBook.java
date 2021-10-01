package bletch.tektopiainformation.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import bletch.common.utils.Font;
import bletch.common.utils.RenderUtils;
import bletch.common.utils.StringUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModDetails;
import bletch.tektopiainformation.core.ModSounds;
import bletch.tektopiainformation.enums.GuiPageType;
import bletch.tektopiainformation.network.data.EconomyData;
import bletch.tektopiainformation.network.data.HomeData;
import bletch.tektopiainformation.network.data.HomesData;
import bletch.tektopiainformation.network.data.ResidentData;
import bletch.tektopiainformation.network.data.ResidentsData;
import bletch.tektopiainformation.network.data.StructureData;
import bletch.tektopiainformation.network.data.StructuresData;
import bletch.tektopiainformation.network.data.VillageData;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.structures.VillageStructureType;

public class GuiTektopiaBook extends GuiScreen {

	private static final String BUTTON_KEY_PREVIOUSPAGE = "previouspage";
	private static final String BUTTON_KEY_NEXTPAGE = "nextpage";
	private static final String BUTTON_KEY_STARTBOOK = "startbook";
	private static final String BUTTON_KEY_ENDBOOK = "endbook";

	private static final String BOOKMARK_KEY_ECONOMY = "economy";
	private static final String BOOKMARK_KEY_HOMES = "homes";
	private static final String BOOKMARK_KEY_PROFESSIONS = "professions";
	private static final String BOOKMARK_KEY_RESIDENTS = "residents";
	private static final String BOOKMARK_KEY_STATISTICS = "statistics";
	private static final String BOOKMARK_KEY_STRUCTURES = "structures";
	private static final String BOOKMARK_KEY_VILLAGE = "village";

	private static final int BOOK_WIDTH = 512;
	private static final int BOOK_HEIGHT = 400;
	
	private static final int BOOKMARK_LEFT_X = -17;
	private static final int BOOKMARK_RIGHT_X = 488;
	private static final int BOOKMARK_TOP_Y = 16;	
	
	private static final int BOOKMARK_WIDTH = 35;
	private static final int BOOKMARK_HEIGHT = 26;
	private static final int BOOKMARK_SPACE_Y = 2;

	private static final int PROFESSION_WIDTH = 56;
	private static final int PROFESSION_HEIGHT = 90;

	private static final int PAGE_LEFTPAGE_LEFTMARGIN_X = 35;
	private static final int PAGE_LEFTPAGE_RIGHTMARGIN_X = 247;
	private static final int PAGE_LEFTPAGE_WIDTH = PAGE_LEFTPAGE_RIGHTMARGIN_X - PAGE_LEFTPAGE_LEFTMARGIN_X;
	private static final int PAGE_LEFTPAGE_CENTER_X = PAGE_LEFTPAGE_LEFTMARGIN_X + (PAGE_LEFTPAGE_WIDTH / 2);
	private static final int PAGE_LEFTPAGE_LEFTCENTER_X = PAGE_LEFTPAGE_LEFTMARGIN_X + (PAGE_LEFTPAGE_WIDTH / 4);
	
	private static final int PAGE_RIGHTPAGE_LEFTMARGIN_X = 268;
	private static final int PAGE_RIGHTPAGE_RIGHTMARGIN_X = 480;
	private static final int PAGE_RIGHTPAGE_WIDTH = PAGE_RIGHTPAGE_RIGHTMARGIN_X - PAGE_RIGHTPAGE_LEFTMARGIN_X;
	private static final int PAGE_RIGHTPAGE_CENTER_X = PAGE_RIGHTPAGE_LEFTMARGIN_X + (PAGE_RIGHTPAGE_WIDTH / 2);
	private static final int PAGE_RIGHTPAGE_LEFTCENTER_X = PAGE_RIGHTPAGE_LEFTMARGIN_X + (PAGE_RIGHTPAGE_WIDTH / 4);
	
	private static final int PAGE_HEADER_Y = 20;
	private static final int PAGE_BODY_Y = 35;
	private static final int PAGE_FOOTER_Y = 335;
	
	private static final int LABEL_TRAILINGSPACE_X = 10;
	
	private static final int LINE_SPACE_Y = 5;
	
	private static final int LINES_PER_PAGE = 20;
	private static final int BEDS_PER_PAGE = 5;
	private static final int ADDITIONALPROFESSIONS_PER_PAGE = 4;
	private static final int STATRESIDENTS_PER_PAGE = 16;
	private static final int SALESHISTORY_PER_PAGE = 16;
	
	private static final ResourceLocation book = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/gui_book.png");
	private static final ResourceLocation bookmarkLeft = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/bookmark_left.png");
	private static final ResourceLocation bookmarkRight = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/bookmark_right.png");
	private static final ResourceLocation buttonPreviousPage = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_previous.png");
	private static final ResourceLocation buttonNextPage = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_next.png");
	private static final ResourceLocation buttonStartBook = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_start.png");
	private static final ResourceLocation buttonEndBook = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_end.png");
	
	private VillageData villageData;
	private HashMap<String, ResourceLocation> bookmarkResources;
	private ArrayList<GuiPage> pages;
	private ArrayList<GuiBookmark> bookmarks;
	private ArrayList<GuiButton> buttons;
	private ArrayList<GuiTooltip> tooltips;
	
	private int leftPageIndex;
	
	private float scale;
	private int x;
	private int y;
	
	public GuiTektopiaBook(VillageData villageData) {
		this.villageData = villageData;
		this.pages = new ArrayList<GuiPage>(); 
		this.bookmarks = new ArrayList<GuiBookmark>();
		this.buttons = new ArrayList<GuiButton>();
		this.tooltips = new ArrayList<GuiTooltip>();
		
		this.scale = 1.0F;
		
		setLeftPageIndex(0);
		createBookmarkResources();
		createPages();
	}
	
	public VillageData getVillageData() {
		return this.villageData;
	}	
	
	public int getLeftPageIndex() {
		return this.leftPageIndex;
	}	
	
	public int getRightPageIndex() {
		return this.leftPageIndex + 1;
	}
	
	public boolean isStartOfBook() {
		return this.getLeftPageIndex() <= this.pages.stream().mapToInt(p -> p.getPageIndex()).min().orElse(0);
	}
	
	public boolean isEndOfBook() {
		return this.getRightPageIndex() >= this.pages.stream().mapToInt(p -> p.getPageIndex()).max().orElse(0);
	}
	
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		// calculate display scale
		this.scale = 10.0F;
		this.scale = Math.min(this.scale, (float)this.width / (float)BOOK_WIDTH);
		this.scale = Math.min(this.scale, (float)this.height / (float)BOOK_HEIGHT);
		
		// calculate background display area 
		this.x = (int) Math.max(1, ((this.width / this.scale) - BOOK_WIDTH) / 2); 
		this.y = (int) Math.max(1, ((this.height / this.scale)  - BOOK_HEIGHT) / 2) + 10;

		this.tooltips.clear();

        createBookmarks();
		createButtons();
       
		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(this.scale, this.scale, 1.0F);

			// draw book background
	        mc.getTextureManager().bindTexture(book);
	        drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
	    	
	        // draw the bookmarks
	        for (GuiBookmark bookmark : this.bookmarks) {
	        	if (bookmark.getBackground() != null && bookmark.getBackground().getTexture() != null) {
		        	mc.getTextureManager().bindTexture(bookmark.getBackground().getTexture());
		        	drawModalRectWithCustomSizedTexture(bookmark.getBackground().getLeft(), bookmark.getBackground().getTop(), bookmark.getBackground().getTextureLeft(), bookmark.getBackground().getTextureTop(), bookmark.getBackground().getWidth(), bookmark.getBackground().getHeight(), bookmark.getBackground().getTextureWidth(), bookmark.getBackground().getTextureHeight());
	        	}
	        	
	        	if (bookmark.getIcon() != null && bookmark.getIcon().getTexture() != null) {
		        	mc.getTextureManager().bindTexture(bookmark.getIcon().getTexture());
		        	drawModalRectWithCustomSizedTexture(bookmark.getIcon().getLeft(), bookmark.getIcon().getTop(), bookmark.getIcon().getTextureLeft(), bookmark.getIcon().getTextureTop(), bookmark.getIcon().getWidth(), bookmark.getIcon().getHeight(), bookmark.getIcon().getTextureWidth(), bookmark.getIcon().getTextureHeight());
	        	}
	        }
	        
	        // draw the buttons
	        for (GuiButton button : this.buttons) {
	        	if (button.getIcon() != null && button.getIcon().getTexture() != null) {
		        	mc.getTextureManager().bindTexture(button.getIcon().getTexture());
		        	drawModalRectWithCustomSizedTexture(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getTextureLeft(), button.getIcon().getTextureTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), button.getIcon().getTextureWidth(), button.getIcon().getTextureHeight());
	        	}
	        }

	        // this is to force something to the screen
	    	Font.small.printLeft("", this.x, this.y);
	    	
	        drawPages(mouseX, mouseY, partialTicks);

	        GlStateManager.popMatrix();

			// display any tooltips
    		for (GuiTooltip tooltip : this.tooltips) {
    			if (tooltip != null && tooltip.withinBounds(mouseX, mouseY, this.scale)) {
    				drawHoveringText(tooltip.getTooltip(), mouseX, mouseY);
    				break;
    			}
    		}	        
        }
        
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
	@Override
    public void initGui() {
		super.initGui();
	}
	
	public void handleMouseInput() throws IOException{
		super.handleMouseInput();
		
		// note to self(signum returns +1 for positive numbers and -1 for negative numbers, 0 for 0..)
        int pageScroll = Integer.signum(Mouse.getEventDWheel());
        if (pageScroll != 0) {
        	setLeftPageIndex(this.leftPageIndex + -(pageScroll * 2));
    		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
        }
	}
	
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if (mouseButton == 0) {
    		for (GuiBookmark bookmark : this.bookmarks) {
    			if (bookmark == null) {
    				continue;
    			}
    			
    			// check if we have clicked on one of the bookmarks
    			if (bookmark != null && bookmark.withinBackgroundBounds(mouseX, mouseY)) {
    				actionPerformed(bookmark);
    				break;
    			}
    			else if (bookmark != null && bookmark.withinIconBounds(mouseX, mouseY)) {
    				actionPerformed(bookmark);
    				break;
    			}
    		}
    		
    		for (GuiButton button : this.buttons) {
    			if (button == null) {
    				continue;
    			}

    			// check if we have clicked on one of the buttons
    			if (button.withinIconBounds(mouseX, mouseY) ) {
    				actionPerformed(button);
    				break;
    			}
    		}
    	}
    	
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    }
   
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
    	this.tooltips.clear();
    	this.tooltips = null;
    	
    	this.bookmarks.clear();
    	this.bookmarks = null;
    	
    	this.pages.clear();
    	this.pages = null;
    	
    	this.bookmarkResources.clear();
    	this.bookmarkResources = null;
    	
    	this.villageData = null;
    }
    
    private void createBookmarkResources() {
    	this.bookmarkResources = new HashMap<String, ResourceLocation>();
    	
    	this.bookmarkResources.put(BOOKMARK_KEY_ECONOMY, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_economy.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_HOMES, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_home.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_PROFESSIONS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_profession.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_RESIDENTS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_resident.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_STATISTICS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_statistics.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_STRUCTURES, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_structure.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_VILLAGE, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_summary.png"));
    }
    
    private void createBookmarks() {
    	final int[] tXL = new int[] { this.x + BOOKMARK_LEFT_X };
    	final int[] tXR = new int[] { this.x + BOOKMARK_RIGHT_X };
    	final int[] tY = new int[] { this.y + BOOKMARK_TOP_Y };
    	
		this.bookmarks.clear();
		
		this.pages.stream()
			.filter(p -> p.getBookmarkKey() != "")
			.forEach(p -> {
				GuiBookmark bookmark = new GuiBookmark(p.getBookmarkKey(), p.getPageIndex());

				ResourceLocation icon = null;
				if (this.bookmarkResources.containsKey(p.getBookmarkKey())) {
					icon = this.bookmarkResources.get(p.getBookmarkKey());
				}
				
	        	if (p.getPageIndex() <= this.getLeftPageIndex()) {
	        		bookmark.setBackground(bookmarkLeft, tXL[0], tY[0], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, 0, 0, 75, 75, this.scale);
	        		if (icon != null) {
	        			bookmark.setIcon(icon, tXL[0] + 12, tY[0] + 5, 16, 16, 0, 0, 16, 16, this.scale);
	        			tXL[0] += 1;
	        		}
	        	} else {
	        		bookmark.setBackground(bookmarkRight, tXR[0], tY[0], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, 0, 0, 75, 75, this.scale);
	        		if (icon != null) {
	        			bookmark.setIcon(icon, tXR[0] + 7, tY[0] + 5, 16, 16, 0, 0, 16, 16, this.scale);
	        			tXR[0] += 1;
	        		}
	        	}
	        	this.bookmarks.add(bookmark);
	        	
	        	String tooltipText = bookmark.getDisplayName();
	        	if (tooltipText != null && tooltipText.trim() != "") {
	        		this.tooltips.add(new GuiTooltip(bookmark.getBackground().getLeft(), bookmark.getBackground().getTop(), bookmark.getBackground().getWidth(), bookmark.getBackground().getHeight(), tooltipText));
	        	}
	        	
	        	tY[0] += BOOKMARK_HEIGHT + BOOKMARK_SPACE_Y;
        	});
    }
    
    private void createButtons() {
    	this.buttons.clear();
  
    	GuiButton button = null;
    	String tooltipText = null;

    	if (!this.isStartOfBook()) {
    		int tX = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + 4;
    		int tY = this.y + PAGE_FOOTER_Y - 5;
    		
	    	button = new GuiButton(BUTTON_KEY_STARTBOOK);
	    	button.setIcon(buttonStartBook, tX, tY, 16, 16, 0, 0, 16, 16, this.scale);
	    	this.buttons.add(button);
	
	    	tooltipText = button.getDisplayName();
	    	if (tooltipText != null && tooltipText.trim() != "") {
	    		this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
	    	}
	    	
	    	tX += button.getIcon().getWidth() + 4;
	    	
	    	button = new GuiButton(BUTTON_KEY_PREVIOUSPAGE);
	    	button.setIcon(buttonPreviousPage, tX, tY, 16, 16, 0, 0, 16, 16, this.scale);
	    	this.buttons.add(button);
	
	    	tooltipText = button.getDisplayName();
	    	if (tooltipText != null && tooltipText.trim() != "") {
	    		this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
	    	}
	    	
	    	tX += button.getIcon().getWidth() + 4;
    	}
    	
    	if (!this.isEndOfBook()) {
    		int tX = this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - 20;
    		int tY = this.y + PAGE_FOOTER_Y - 5;
	    	
	    	button = new GuiButton(BUTTON_KEY_ENDBOOK);
	    	button.setIcon(buttonEndBook, tX, tY, 16, 16, 0, 0, 16, 16, this.scale);
	    	this.buttons.add(button);
	
	    	tooltipText = button.getDisplayName();
	    	if (tooltipText != null && tooltipText.trim() != "") {
	    		this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
	    	}
	    	
	    	tX -= button.getIcon().getWidth() + 4;
	    	
    		button = new GuiButton(BUTTON_KEY_NEXTPAGE);
	    	button.setIcon(buttonNextPage, tX, tY, 16, 16, 0, 0, 16, 16, this.scale);
	    	this.buttons.add(button);
	
	    	tooltipText = button.getDisplayName();
	    	if (tooltipText != null && tooltipText.trim() != "") {
	    		this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
	    	}
	    	
	    	tX -= button.getIcon().getWidth() + 4;
    	}
    }
    
    private void createPages() {
   	
    	this.pages.clear();
 
    	if (this.villageData == null) {
    		return;
    	}

    	EconomyData economyData = this.villageData.getEconomyData();
    	HomesData homesData = this.villageData.getHomesData();
    	ResidentsData residentsData = this.villageData.getResidentsData();
    	StructuresData structuresData = this.villageData.getStructuresData();

    	int startPageIndex = 0;
    	int pageIndex = 0;

    	// inside cover page page
    	this.pages.add(new GuiPage(GuiPageType.INSIDECOVER, pageIndex++, getPageKey("", 0)));

    	// title page
    	this.pages.add(new GuiPage(GuiPageType.TITLE, pageIndex++, getPageKey("", 0)));

    	// village title page
    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_VILLAGE, 0), BOOKMARK_KEY_VILLAGE));

    	// village pages
    	this.pages.add(new GuiPage(GuiPageType.VILLAGE, pageIndex++, getPageKey("", 0)));
    	
    	if (structuresData != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// structure title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_STRUCTURES, 0), BOOKMARK_KEY_STRUCTURES));

	    	List<VillageStructureType> structureTypes = TektopiaUtils.getVillageStructureTypes();
    		
	    	// structure type pages
    		if (structuresData.getStructureTypeCounts() != null) {
    			int count = structureTypes.size();
    			
        		if (count > 0) {
    	    		int pages = count / LINES_PER_PAGE;
    	    		if (count % LINES_PER_PAGE > 0) {
    	    			pages++;
    	    		}
    	    		
    	    		for (int page = 0; page < pages; page++) {
	    				this.pages.add(new GuiPage(GuiPageType.STRUCTURE, pageIndex++, getPageKey("", page)));
    	    		}
        		}
    		}
    		
	    	// structure pages
    		if (structuresData.getStructures() != null) {
        		for (VillageStructureType structureType : structureTypes) {
        			List<StructureData> structures = structuresData.getStructuresByType(structureType);

        			StructureData structure = structures == null || this.villageData.getFramePosition() == null ? null : structures.stream()
        					.filter(s -> s.getFramePosition() != null && this.villageData.getFramePosition().equals(s.getFramePosition()))
        					.findFirst().orElse(null);
        			int indexOf = structure == null ? -1 : structures.indexOf(structure);
        			
    	    		int count = structures == null || structures.size() == 0 ? 0 : structures.size();
    	    		if (count > 0) {
    		    		int pages = count / LINES_PER_PAGE;
    		    		if (count % LINES_PER_PAGE > 0) {
    		    			pages++;
    		    		}
    		    		
    		    		for (int page = 0; page < pages; page++) {
    	        			// check if the structure owns the frame position
    	        			if (indexOf >= 0 && indexOf <= ((page + 1) * LINES_PER_PAGE)) {
	        					startPageIndex = pageIndex;
    	        			} 
    	        			
    			        	this.pages.add(new GuiPage(GuiPageType.STRUCTURETYPE, pageIndex++, getPageKey(structureType.name(), page)));
    		    		}
    	    		}
        		}
    		}
    	}
    	
    	if (homesData != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// home title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_HOMES, 0), BOOKMARK_KEY_HOMES));
	    	
    		List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();
    		
        	// home type pages
    		if (homesData.getHomeTypeCounts() != null) {
    			int count = homeTypes.size();
    			
        		if (count > 0) {
    	    		int pages = count / LINES_PER_PAGE;
    	    		if (count % LINES_PER_PAGE > 0) {
    	    			pages++;
    	    		}
    	    		
    	    		for (int page = 0; page < pages; page++) {
	    				this.pages.add(new GuiPage(GuiPageType.HOME, pageIndex++, getPageKey("", page)));
    	    		}
        		}
    		}
    		
    		if (homesData.getHomes() != null) {
            	// home type pages
        		for (VillageStructureType homeType : homeTypes) {
        			List<HomeData> homes = homesData.getHomesByType(homeType);

    	    		int count = homes == null || homes.size() == 0 ? 0 : homes.size();
    	    		if (count > 0) {
    		    		int pages = count / LINES_PER_PAGE;
    		    		if (count % LINES_PER_PAGE > 0) {
    		    			pages++;
    		    		}
    		    		
    		    		for (int page = 0; page < pages; page++) {
    			        	this.pages.add(new GuiPage(GuiPageType.HOMETYPE, pageIndex++, getPageKey(homeType.name(), page)));
    		    		}
    	    		}
        		}
        		
            	// home pages
        		for (HomeData homeData : homesData.getHomes()) {
        			int count = homeData.getMaxBeds();
        			
            		if (count > 0) {
        	    		int pages = count / BEDS_PER_PAGE;
        	    		if (count % BEDS_PER_PAGE > 0) {
        	    			pages++;
        	    		}
        	    		
        	    		for (int page = 0; page < pages; page++) {
    	        			// check if the home owns the frame position
    	        			if (page == 0 && this.villageData.getFramePosition() != null && homeData.getFramePosition() != null && this.villageData.getFramePosition().equals(homeData.getFramePosition())) {
	        					startPageIndex = pageIndex;
    	        			} 

    	        			this.pages.add(new GuiPage(GuiPageType.HOME, pageIndex++, getPageKey(homeData.getHomeId().toString(), page)));
	        			}
            		}
        		}
    		}
		}

		if (TektopiaUtils.getProfessionTypes() != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// profession title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_PROFESSIONS, 0), BOOKMARK_KEY_PROFESSIONS));
	    	
	    	// profession type pages
			int count = TektopiaUtils.getProfessionTypes().size();
			if (count > 0) {
	    		int pages = count / LINES_PER_PAGE;
	    		if (count % LINES_PER_PAGE > 0) {
	    			pages++;
	    		}
	    		
	    		for (int page = 0; page < pages; page++) {
		        	this.pages.add(new GuiPage(GuiPageType.PROFESSION, pageIndex++, getPageKey("", page)));
	    		}
			}
			
	    	// profession pages
			if (residentsData != null) {
				for (ProfessionType professionType : TektopiaUtils.getProfessionTypes()) {
					count = residentsData.getProfessionTypeCount(professionType);
					if (count > 0) {
			    		int pages = count / LINES_PER_PAGE;
			    		if (count % LINES_PER_PAGE > 0) {
			    			pages++;
			    		}
			    		
			    		for (int page = 0; page < pages; page++) {
				        	this.pages.add(new GuiPage(GuiPageType.PROFESSIONTYPE, pageIndex++, getPageKey(professionType.name(), page)));
			    		}
					}
				}
			}
		}
    	
    	if (residentsData != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// resident title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_RESIDENTS, 0), BOOKMARK_KEY_RESIDENTS));
	    	
	    	// resident list
	    	int count = residentsData.getResidentsCount();
			if (count > 0) {
	    		int pages = count / LINES_PER_PAGE;
	    		if (count % LINES_PER_PAGE > 0) {
	    			pages++;
	    		}
	    		
	    		for (int page = 0; page < pages; page++) {
    		    	this.pages.add(new GuiPage(GuiPageType.RESIDENT, pageIndex++, getPageKey("", page)));
	    		}
			}
    	
	    	// resident pages
    		if (residentsData.getResidents() != null) {
        		for (ResidentData residentData : residentsData.getResidents()) {
    	        	// check for the resident
        			if (this.villageData.getResidentId() > 0 && this.villageData.getResidentId() == residentData.getResidentId()) {
        				startPageIndex = pageIndex;
        			}
        			// check if the resident owns the bed position
        			if (this.villageData.getBedPosition() != null && residentData.getBedPosition() != null && this.villageData.getBedPosition().equals(residentData.getBedPosition())) {
        				startPageIndex = pageIndex;
        			}    	        	
        			
        			this.pages.add(new GuiPage(GuiPageType.RESIDENT, pageIndex++, getPageKey("" + residentData.getResidentId(), 0)));
        		}
    		}
    	}
    	
    	if (residentsData != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// statistic title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_STATISTICS, 0), BOOKMARK_KEY_STATISTICS));
	    	
	    	// summary pages
        	this.pages.add(new GuiPage(GuiPageType.STATS, pageIndex++, getPageKey("", 0)));
        	
        	// happiness
        	Map<Integer, List<ResidentData>> happinessMap = residentsData.getResidentHappinessStatistics();

        	if (happinessMap != null) {
        		for (Entry<Integer, List<ResidentData>> happinessEntry : happinessMap.entrySet()) {
        			int count = happinessEntry.getValue().size();
        			if (count > 0) {
        	    		int pages = count / STATRESIDENTS_PER_PAGE;
        	    		if (count % STATRESIDENTS_PER_PAGE > 0) {
        	    			pages++;
        	    		}
        	    		
        	    		for (int page = 0; page < pages; page++) {
                			this.pages.add(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("happiness", happinessEntry.getKey(), page)));
        	    		}
        			} else {
            			this.pages.add(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("happiness", happinessEntry.getKey(), 0)));
        			}
        		}        		
        	}
    		
    		// hunger
    		Map<Integer, List<ResidentData>> hungerMap = residentsData.getResidentHungerStatistics();
    		
    		if (hungerMap != null) {
	    		for (Entry<Integer, List<ResidentData>> hungerEntry : hungerMap.entrySet()) {
	    			int count = hungerEntry.getValue().size();
	    			if (count > 0) {
	    	    		int pages = count / STATRESIDENTS_PER_PAGE;
	    	    		if (count % STATRESIDENTS_PER_PAGE > 0) {
	    	    			pages++;
	    	    		}
	    	    		
	    	    		for (int page = 0; page < pages; page++) {
	            			this.pages.add(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("hunger", hungerEntry.getKey(), page)));
	    	    		}
	    			} else {
	        			this.pages.add(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("hunger", hungerEntry.getKey(), 0)));
	    			}
	    		}
    		}
    	}
    	    	
    	if (economyData != null) {
    		if (pageIndex % 2 != 0)
    			this.pages.add(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));
    		
	    	// economy title page
	    	this.pages.add(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_ECONOMY, 0), BOOKMARK_KEY_ECONOMY));
	    	
	    	// sales history pages
			int count = economyData.getMerchantSales();
			if (count > 0) {
	    		int pages = count / SALESHISTORY_PER_PAGE;
	    		if (count % SALESHISTORY_PER_PAGE > 0) {
	    			pages++;
	    		}
	    		
	    		for (int page = 0; page < pages; page++) {
    		    	this.pages.add(new GuiPage(GuiPageType.ECONOMY, pageIndex++, getPageKey("saleshistory", page)));
	    		}
			} else {
				this.pages.add(new GuiPage(GuiPageType.ECONOMY, pageIndex++, getPageKey("saleshistory", 0)));
			}
    	}
   	
    	setLeftPageIndex(startPageIndex);
    }
   
    private void drawHeader(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
    	GL11.glColor4f(0f, 0f, 0f, 1f);
    	
        String headerText = TextUtils.translate("tektopiaBook.name");

        if (headerText != null && headerText.trim() != "") {
        	if (guiPage.isLeftPage()) {
            	Font.small.printLeft(headerText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, this.y + PAGE_HEADER_Y);
        	}
        	
        	if (guiPage.isRightPage()) {
            	Font.small.printRight(headerText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, this.y + PAGE_HEADER_Y);
        	}
        }
    }

    private void drawFooter(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        GL11.glColor4f(0f, 0f, 0f, 1f);

        if (guiPage.isLeftPage()) {
            Font.small.printCentered(this.leftPageIndex, this.x + PAGE_LEFTPAGE_CENTER_X, this.y + PAGE_FOOTER_Y); 
        }

        if (guiPage.isRightPage()) {
            Font.small.printCentered(this.leftPageIndex + 1, this.x + PAGE_RIGHTPAGE_CENTER_X, this.y + PAGE_FOOTER_Y); 
        }
    }
    
    private void drawPages(int mouseX, int mouseY, float partialTicks) {
    	GL11.glColor4f(0f, 0f, 0f, 1f);

    	final int[] pageIndex = { this.leftPageIndex };
    	
    	for (int index = 0; index < 2; index++) {
    		GuiPage guiPage = this.pages.stream()
    				.filter(p -> p.getPageIndex() == pageIndex[0])
    				.findFirst().orElse(null);
    		
        	if (guiPage != null) {
            	switch (guiPage.getGuiPageType()) {
            	case BLANK:
            		drawPageBlank(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case ECONOMY:
            		drawPageEconomy(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case HOME:
            		drawPageHome(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case HOMETYPE:
            		drawPageHomeType(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case PROFESSION:
            		drawPageProfession(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case PROFESSIONTYPE:
            		drawPageProfessionType(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case RESIDENT:
            		drawPageResident(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case STATS:
            		drawPageStatistics(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case STRUCTURE:
            		drawPageStructure(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case STRUCTURETYPE:
            		drawPageStructureType(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case SUMMARY:
            		drawPageSummary(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case TITLE:
            		drawPageTitle(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case VILLAGE:
            		drawPageVillage(mouseX, mouseY, partialTicks, guiPage);
            		break;
            	case INSIDECOVER:
    			default:
    				break;
        		}
        	}
        	
        	pageIndex[0]++;
    	}
	}
    
    private void drawPageBlank(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage);
    }
    
    private void drawPageEconomy(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage);
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	EconomyData economyData = this.villageData.getEconomyData();
        String continued = TextUtils.translate("tektopiaBook.continued");
        
        if (dataKey[0].equals("saleshistory")) {
        	// sales history

            String header = TextUtils.translate("tektopiaBook.economy.salesHistory");
        	
            if (header != null && header.trim() != "") {
            	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
            		header += " " + continued;
            	}
            	header = TextFormatting.DARK_BLUE + header;
            	
            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }

        	if (economyData != null) {
        		List<ItemStack> salesHistory = economyData.getSalesHistory();
        		
            	final int[] maxLength = { 0 };
            	maxLength[0] += Font.small.getStringWidth("" + salesHistory.size());
            	maxLength[0] += LABEL_TRAILINGSPACE_X;
            	
	        	int page = 0;
	        	try {
	        		page = Integer.parseInt(dataKey[1]);
	        	}
	        	catch (NumberFormatException e) {
	        		page = 0;
	        	}
	        	int startIndex = page * SALESHISTORY_PER_PAGE;
	        	int index = 0;
            	
            	String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
            	String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
            	String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");
            	
    			if (guiPage.isLeftPage()) {
                    Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 20, y);
                    Font.small.printRight(countHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 20, y);  
                    Font.small.printRight(countHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y); 
            	}

            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            	
        		for (ItemStack itemStack : salesHistory) {
        			if (index >= startIndex && index < startIndex + SALESHISTORY_PER_PAGE) {
        				
            			String indexName = "" + (index + 1);
            			String itemName = itemStack.getDisplayName();
            			String itemCount = "" + (itemStack.getCount() > 1 ? itemStack.getCount() : "0");
            			
                    	if (guiPage.isLeftPage()) {
    	                    Font.small.printRight(indexName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                            Font.small.printLeft(itemName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 20, y); 
                            Font.small.printRight(itemCount, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

    	                    RenderUtils.renderItemIntoGUI(itemStack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 10, y - 5);
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                    		Font.small.printRight(indexName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                            Font.small.printLeft(itemName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 20, y); 
                            Font.small.printRight(itemCount, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

                    		RenderUtils.renderItemIntoGUI(itemStack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 10, y - 5);
                    	}                   	
                    	
    	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + 2;           			
        			}
        			index++;
        		}
        	}
        }
    }
    
    private void drawPageHome(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	HomesData homesData = this.villageData.getHomesData();
        String continued = TextUtils.translate("tektopiaBook.continued");
        
        if (dataKey[0].equals("")) {
        	// home type

            String typeHeader = TextUtils.translate("tektopiaBook.homes.hometypes");
            
            if (typeHeader != null && typeHeader.trim() != "") {
            	typeHeader = TextFormatting.DARK_BLUE + typeHeader;

            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            } 
                
    		Map<VillageStructureType, Integer> homeTypeCounts = homesData != null ? homesData.getHomeTypeCounts() : null;
        	
            if (homeTypeCounts != null) {
            	final int[] maxLength = { 0 };
            	
            	int page = 0;
            	try {
            		page = Integer.parseInt(dataKey[1]);
            	}
            	catch (NumberFormatException e) {
            		page = 0;
            	}
            	int startIndex = page * LINES_PER_PAGE;
            	int index = 0;
            	
            	String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
            	String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");
            	
            	maxLength[0] += Font.small.getStringWidth(countHeader);
            	
    			if (guiPage.isLeftPage()) {
                    Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}

            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            	
            	for (VillageStructureType homeType : TektopiaUtils.getHomeStructureTypes()) {
            		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
            			String typeName = getStructureTypeName(homeType);
            			int typeCount = 0;
            			
            			if (homeTypeCounts.containsKey(homeType)) {
            				typeCount = homeTypeCounts.get(homeType);
            			}
            			
            			if (guiPage.isLeftPage()) {
    	                    Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
    	                    Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[0], y); 
    	            	}
    	            	
    	            	if (guiPage.isRightPage()) {
    	                    Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
    	                    Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y); 
    	            	}

    	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            		}  
            		index++;
            	}
            }
        	
        } else {
        	// home
        	
        	UUID homeId = null;
        	try {
        		homeId = UUID.fromString(dataKey[0]);
        	}
        	catch (IllegalArgumentException e) {
        		homeId = null;
        	}
        	
        	HomeData homeData = homesData.getHomeById(homeId);
        	if (homeData != null) {

                String header = homeData.getStructureTypeName();
            	
                if (header != null && header.trim() != "") {
                	// check if this is the villager we clicked on
        			if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(homeData.getFramePosition())) {
        				header = TextFormatting.UNDERLINE + header;
        			}      
        			if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
                		header += " " + continued;
                	}
                	header = TextFormatting.DARK_BLUE + header;
               	
                	if (guiPage.isLeftPage()) {
                        Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                } 

                String framePositionLabel = TextUtils.translate("tektopiaBook.homes.frameposition");
                String framePositionText = "";

                if (framePositionLabel != null && framePositionLabel.trim() != "") {
                	framePositionText += formatBlockPos(homeData.getFramePosition());
                	
                	if (guiPage.isLeftPage()) {
                        Font.small.printLeft(framePositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printLeft(framePositionText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printLeft(framePositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                        Font.small.printLeft(framePositionText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                } 

                String floorTilesLabel = TextUtils.translate("tektopiaBook.homes.floortiles");
                String floorTilesText = "";

                if (floorTilesLabel != null && floorTilesLabel.trim() != "") {
                	floorTilesText += "" + homeData.getFloorTileCount();
                	
                	if (guiPage.isLeftPage()) {
                        Font.small.printLeft(floorTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                        Font.small.printRight(floorTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printLeft(floorTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printRight(floorTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                }
            	
                String residentsLabel = TextUtils.translate("tektopiaBook.homes.totalresidents");
                String residentsText = "";

                if (residentsLabel != null && residentsLabel.trim() != "") {
                	residentsText += "" + homeData.getResidentsCount();
                	
                	if (guiPage.isLeftPage()) {
                        Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
                	}
                }
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
            	if (dataKey[1].equals("0")) {
                	String adultLabel = TextUtils.translate("tektopiaBook.homes.adults");
                	String adultText = "";
                	
                	if (adultLabel != null && adultLabel.trim() != "") {
                		adultText += "" + homeData.getAdultCount();
                    	
                    	if (guiPage.isLeftPage()) {
                			Font.small.printLeft(adultLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(adultText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                			Font.small.printLeft(adultLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(adultText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
                    	}
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
                	String childLabel = TextUtils.translate("tektopiaBook.homes.children");
                	String childText = "";
                	
                	if (childLabel != null && childLabel.trim() != "") {
                		childText += "" + homeData.getChildCount();
                    	
                    	if (guiPage.isLeftPage()) {
                			Font.small.printLeft(childLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(childText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                			Font.small.printLeft(childLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(childText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
                    	}
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
                	String maleLabel = TextUtils.translate("tektopiaBook.homes.males");
                	String maleText = "";
                	
                	if (maleLabel != null && maleLabel.trim() != "") {
                		maleText += "" + homeData.getMaleCount();
                    	
                    	if (guiPage.isLeftPage()) {
                			Font.small.printLeft(maleLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(maleText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                			Font.small.printLeft(maleLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(maleText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
                    	}
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
                	String femaleLabel = TextUtils.translate("tektopiaBook.homes.females");
                	String femaleText = "";
                	
                	if (femaleLabel != null && femaleLabel.trim() != "") {
                		femaleText += "" + homeData.getFemaleCount();
                    	
                    	if (guiPage.isLeftPage()) {
                			Font.small.printLeft(femaleLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                			Font.small.printRight(femaleText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                			Font.small.printLeft(femaleLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                			Font.small.printRight(femaleText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
                    	}
                	}
                } else {

                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;             
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            	y += 10;

                String bedsText = TextUtils.translate("tektopiaBook.homes.beds");
            	
                if (bedsText != null && bedsText.trim() != "") {
                	bedsText = TextFormatting.DARK_BLUE + bedsText;

                	if (guiPage.isLeftPage()) {
                        Font.small.printLeft(bedsText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printLeft(bedsText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                } 

            	List<BlockPos> bedPositions = homeData.getBedPositions(); 
            	
            	final int[] maxLength = { 0 };
            	maxLength[0] += LABEL_TRAILINGSPACE_X;
            	
            	int page = 0;
            	try {
            		page = Integer.parseInt(dataKey[1]);
            	}
            	catch (NumberFormatException e) {
            		page = 0;
            	}
            	int startIndex = page * BEDS_PER_PAGE;

            	String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
            	String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
            	String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
            	String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");
            	
            	if (guiPage.isLeftPage()) {
            		Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                	Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
            	}

            	if (guiPage.isRightPage()) {
                    Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                	Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
            	
            	for (int bedIndex = 0; bedIndex < homeData.getMaxBeds(); bedIndex++) {
            		if (bedIndex >= startIndex && bedIndex < startIndex + BEDS_PER_PAGE) {
                		String bedText = "" + (bedIndex + 1);
                		String bedPositionText = "";
                		
                		String residentName = "";
                		String residentProfession = "";
                		String residentLevel = "";
                		
            			BlockPos bedPosition = bedIndex < bedPositions.size() ? bedPositions.get(bedIndex) : null;
            			if (bedPosition == null) {
            				bedPositionText += TextFormatting.DARK_RED + TextUtils.translate("tektopiaBook.missing");
            				
            			} else {
            				bedPositionText += "" + formatBlockPos(bedPosition);
            				
            				ResidentData residentData = homeData.getResidentByBedPosition(bedPosition);

                    		if (residentData != null) {
                    			residentName += formatResidentName(residentData.isMale(), residentData.getResidentName(), true);
                				residentProfession += TextUtils.translate("entity." + residentData.getProfessionType().name + ".name");
                    			
                    			switch (residentData.getProfessionType()) {
                    			case CHILD:
                    			case NITWIT:
                    				break;
                    			default:                       			
                    				residentLevel = formatResidentLevel(residentData.getLevel(), residentData.getBaseLevel(), false, false);
                        			break;
                    			}
                    		} else {
                    			residentName = TextFormatting.GOLD + TextUtils.translate("tektopiaBook.empty");
                    		}
            			}   
            			
            			if (guiPage.isLeftPage()) {
    	                    Font.small.printRight(bedText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
    	                    Font.small.printLeft(bedPositionText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
    	                	Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_CENTER_X, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
                    		
        	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	                    
    	                    Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X, y);
	                    }
    	            	
    	            	if (guiPage.isRightPage()) {
    	                    Font.small.printRight(bedText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
    	                    Font.small.printLeft(bedPositionText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
    	                	Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
                    		
        	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	                    
    	                    Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
	                    }
                		
    	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            		}
            	}
            }
        }
    }
    
    private void drawPageHomeType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 

    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		VillageStructureType homeType = VillageStructureType.valueOf(dataKey[0]);

		HomesData homesData = this.villageData.getHomesData();
		Map<VillageStructureType, Integer> homeTypeCounts = homesData != null ? homesData.getHomeTypeCounts() : null;
		List<HomeData> homes = homesData != null ? homesData.getHomesByType(homeType) : null;
				
		String typeName = getStructureTypeName(homeType);
        String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");
        
        if (typeName != null && typeName.trim() != "") {
        	typeName += " " + summary;
        	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
        		typeName += " " + continued;
        	}
        	typeName = TextFormatting.DARK_BLUE + typeName;
        	
        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

        if (dataKey[1].equals("0")) {
            String totalLabel = TextUtils.translate("tektopiaBook.hometypes.totalhomes");
            String totalText = "";

            if (totalLabel != null && totalLabel.trim() != "") {
            	if (homeTypeCounts != null && homeTypeCounts.containsKey(homeType)) {
            		totalText += "" + homeTypeCounts.get(homeType);
            	}
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
            	}
            }
            
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

        	String residentsLabel = TextUtils.translate("tektopiaBook.homes.totalresidents");
        	String residentsText = "";

            if (residentsLabel != null && residentsLabel.trim() != "") {
            	if (this.villageData.getResidentsData() != null)
            		residentsText += "" + homesData.getResidentCountByType(homeType);
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            }           
        } else {
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        }
    	
    	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	y += 10;

    	String header = TextUtils.translate("tektopiaBook.hometypes.frameposition");
        
        if (header != null && header.trim() != "") {
        	header = TextFormatting.DARK_BLUE + header;

        	if (guiPage.isLeftPage()) {
                Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

		if (homes != null) {
        	final int[] maxLength = { 0 , 0 };
        	maxLength[0] += LABEL_TRAILINGSPACE_X;
        	
        	int page = 0;
        	try {
        		page = Integer.parseInt(dataKey[1]);
        	}
        	catch (NumberFormatException e) {
        		page = 0;
        	}
        	int startIndex = page * LINES_PER_PAGE;
        	int index = 0;

        	String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
        	String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
        	String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.hometypes.residents");
        	String validTextHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.valid");
        	
        	maxLength[1] += Font.small.getStringWidth(residentsHeader);
        	
        	if (guiPage.isLeftPage()) {
        		Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
            	Font.small.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
        	}

        	if (guiPage.isRightPage()) {
                Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
            	Font.small.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

        	for (HomeData home : homes) {
        		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
        			
        			String indexName = "" + (index + 1);
        			String framePosition = formatBlockPos(home.getFramePosition());
        			if (this.villageData.getFramePosition() != null && home.getFramePosition() != null && this.villageData.getFramePosition().equals(home.getFramePosition())) {
        				framePosition = TextFormatting.UNDERLINE + framePosition;
        			}
        			String residentText = "" + (home.getResidentsCount() >= 0 ? home.getResidentsCount() : "0");
        			String validText = "" + (home.isValid() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
        			
                	if (guiPage.isLeftPage()) {
	                    Font.small.printRight(indexName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printLeft(framePosition, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
        				Font.small.printRight(residentText, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 
        				Font.small.printRight(validText, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printRight(indexName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printLeft(framePosition, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
        				Font.small.printRight(residentText, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
        				Font.small.printRight(validText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
                	}
                	
	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
        		}
        		index++;
    		}
		}
    }
  
    private void drawPageProfession(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	ResidentsData residentsData = this.villageData.getResidentsData();
        String continued = TextUtils.translate("tektopiaBook.continued");
    	
        String typeHeader = TextUtils.translate("tektopiaBook.professions.professiontypes");
        
        if (typeHeader != null && typeHeader.trim() != "") {
        	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
        		typeHeader += " " + continued;
        	}
        	typeHeader = TextFormatting.DARK_BLUE + typeHeader;

        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 
        
		Map<ProfessionType, Integer> professionTypeCounts = residentsData != null ? residentsData.getProfessionTypeCounts() : null;

		if (professionTypeCounts != null) {
        	final int[] maxLength = { 0 };
	    	
	    	int page = 0;
	    	try {
	    		page = Integer.parseInt(dataKey[1]);
	    	}
	    	catch (NumberFormatException e) {
	    		page = 0;
	    	}
	    	int startIndex = page * LINES_PER_PAGE;
	    	int index = 0;
        	
        	String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
        	String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");
        	
        	maxLength[0] += Font.small.getStringWidth(countHeader);
        	
			if (guiPage.isLeftPage()) {
                Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
        	}

        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	    	
	    	for (ProfessionType professionType : TektopiaUtils.getProfessionTypes()) {
	    		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
	    			String typeName = getProfessionTypeName(professionType);
	    			int typeCount = 0;
	    			
	    			if (professionTypeCounts.containsKey(professionType)) {
	    				typeCount = professionTypeCounts.get(professionType);
	    			}
	    			
	    			if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[0], y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y); 
	            	}
	
	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	    		}  
	    		index++;
	    	}
	    }
    }
    
    private void drawPageProfessionType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 

    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		ProfessionType professionType = ProfessionType.valueOf(dataKey[0]);
		
		ResidentsData residentsData = this.villageData.getResidentsData();
		Map<ProfessionType, Integer> professionTypeCounts = residentsData != null ? residentsData.getProfessionTypeCounts() : null;
		List<ResidentData> residents = residentsData != null ? residentsData.getResidentsByType(professionType) : null;

		String typeName = getProfessionTypeName(professionType);
		String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");
		
        if (typeName != null && typeName.trim() != "") {
        	typeName += " " + summary;
        	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
        		typeName += " " + continued;
        	}
        	typeName = TextFormatting.DARK_BLUE + typeName;
        	
        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

        if (dataKey[1].equals("0")) {
        	
            String totalLabel = TextUtils.translate("tektopiaBook.professiontypes.total");
            String totalText = "";

            if (totalLabel != null && totalLabel.trim() != "") {
            	if (professionTypeCounts != null && professionTypeCounts.containsKey(professionType)) {
            		totalText += "" + professionTypeCounts.get(professionType);
            	}
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            }
        } 
    	
    	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

    	String header = TextUtils.translate("tektopiaBook.professiontypes.residents");
        
        if (header != null && header.trim() != "") {
        	header = TextFormatting.DARK_BLUE + header;

        	if (guiPage.isLeftPage()) {
                Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

		if (residents != null) {
        	final int[] maxLength = { 0 };
        	maxLength[0] += LABEL_TRAILINGSPACE_X;
        	
        	int page = 0;
        	try {
        		page = Integer.parseInt(dataKey[1]);
        	}
        	catch (NumberFormatException e) {
        		page = 0;
        	}
        	int startIndex = page * LINES_PER_PAGE;
        	int index = 0;

        	String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
        	String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
        	String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");
        	
        	if (guiPage.isLeftPage()) {
            	Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
                Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
				Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
        	}
        	
        	if (guiPage.isRightPage()) {
            	Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
                Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
				Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
        	
        	for (ResidentData resident : residents) {
        		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
        			
        			String residentBed = "";
        			String residentName = "";
            		String residentLevel = "";

            		residentBed = (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
            		residentName = formatResidentName(resident.isMale(), resident.getResidentName(), true);

        			switch (professionType) {
        			case CHILD:
        			case NITWIT:
        				break;
        			default:
        				residentLevel = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), false, false);
            			break;
        			}

                	if (guiPage.isLeftPage()) {
                    	Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
                	}
                	
                	if (guiPage.isRightPage()) {
                    	Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                        Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
                	}
                	
	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
        		}
        		index++;
    		}
		}
	}
    
    private void drawPageResident(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 

    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	ResidentsData residentsData = this.villageData.getResidentsData();
    	StructuresData structuresData = this.villageData.getStructuresData();
    	
        if (dataKey[0].equals("")) {
        	// resident list
        	
            String pageHeader = TextUtils.translate("tektopiaBook.residents.residents");
            
            if (pageHeader != null && pageHeader.trim() != "") {
            	pageHeader = TextFormatting.DARK_BLUE + pageHeader;

            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(pageHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(pageHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            } 
            
            List<ResidentData> residents = residentsData.getResidents();
            
            if (residents != null) {
            	final int[] maxLength = { 0 };
            	maxLength[0] += LABEL_TRAILINGSPACE_X;
            	
            	int page = 0;
            	try {
            		page = Integer.parseInt(dataKey[1]);
            	}
            	catch (NumberFormatException e) {
            		page = 0;
            	}
            	int startIndex = page * LINES_PER_PAGE;
            	int index = 0;

            	String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
            	String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
            	String professionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.profession");
            	String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");
            	
            	if (guiPage.isLeftPage()) {
                	Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
                    Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    Font.small.printLeft(professionHeader, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	if (guiPage.isRightPage()) {
                	Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
                    Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    Font.small.printLeft(professionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
            	
            	for (ResidentData resident : residentsData.getResidents()) {
            		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
            			String residentBed = "";
                		String residentName = "";
                		String residentProfession = "";
                		String residentLevel = "";

                		residentBed = (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
                		residentName = formatResidentName(resident.isMale(), resident.getResidentName(), true);
        				residentProfession += TextUtils.translate("entity." + resident.getProfessionType().name + ".name");
            			
            			switch (resident.getProfessionType()) {
            			case CHILD:
            			case NITWIT:
            				break;
            			default:
            				residentLevel = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), false, false);
                			break;
            			}

            			if (guiPage.isLeftPage()) {
	                    	Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    	Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
	                    	Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
	                    }
    	            	
    	            	if (guiPage.isRightPage()) {
	                    	Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    	Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
	                    	Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
	                    }

    	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            		}  
            		index++;
            	}
            }
        }
        else {
        	// resident
        	
        	int residentId = 0;
        	try {
        		residentId = Integer.parseInt(dataKey[0]);
        	}
        	catch (NumberFormatException e) {
        		residentId = 0;
        	}

        	ResidentData resident = residentId > 0 ? residentsData.getResidentById(residentId) : null;
        	
        	if (resident != null) {
 
                String header = stripTextFormatting(resident.getResidentName());
                
                if (header != null && header.trim() != "") {
                	// check if this is the villager we clicked on
        			if (this.villageData.getResidentId() > 0 && this.villageData.getResidentId() == residentId) {
        				header = TextFormatting.UNDERLINE + header;
        			}      
        			
                	// check if this is the villager that owns the bed we clicked on
            		if (this.villageData.getBedPosition() != null && this.villageData.getBedPosition().equals(resident.getBedPosition())) {
            			header = TextFormatting.UNDERLINE + header;
            		}
            		
        			if (resident.isMale())
        				header = TextFormatting.BLUE + header + " " + TextUtils.SYMBOL_MALE;
        			else
        				header = TextFormatting.LIGHT_PURPLE + header + " " + TextUtils.SYMBOL_FEMALE;

                	if (guiPage.isLeftPage()) {
                        Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                } 
                
        		{
                	int tXL = this.x + (PAGE_LEFTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
                	int tXR = this.x + (PAGE_RIGHTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
                	int tY = y;

	            	GlStateManager.pushMatrix();
        	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	
	            	String gender = resident.isMale() ? "m" : "f";
	                ResourceLocation residentResource = new ResourceLocation(ModDetails.MOD_ID, "textures/professions/" + resident.getProfessionType().name + "_" + gender + ".png");
	                
	                if (residentResource != null) {
		    	        mc.getTextureManager().bindTexture(residentResource);
		                
		                if (guiPage.isLeftPage()) {
		                	drawModalRectWithCustomSizedTexture(tXL, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
		                }
		                
		                if (guiPage.isRightPage()) {
		                	drawModalRectWithCustomSizedTexture(tXR, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
		                }	                	
	                }
	                
        			tXL = this.x + PAGE_LEFTPAGE_CENTER_X;
        			tXR = this.x + PAGE_RIGHTPAGE_CENTER_X;
        			tY = y + 10;

	                // draw equipment slots
	                for (ItemStack piece : resident.getEquipment()) {
	                	if (piece != null && piece != ItemStack.EMPTY) {
	                		List<String> tooltip = piece.getTooltip(null, TooltipFlags.NORMAL);
	                		if (piece.isItemEnchanted() && tooltip != null && tooltip.size() > 0) {
	                			tooltip.set(0, TextFormatting.AQUA + tooltip.get(0));
	                		}

	                		if (guiPage.isLeftPage()) {
	                			RenderUtils.renderItemAndEffectIntoGUI(piece, tXL, tY);
	                			RenderUtils.renderItemOverlayIntoGUI(Font.normal.fontRenderer, piece, tXL, tY);
	                			if (tooltip != null && tooltip.size() > 0) {
	                				this.tooltips.add(new GuiTooltip(tXL, tY, 16, 16, tooltip));
	                			}
	                		}

	                		if (guiPage.isRightPage()) {
	                			RenderUtils.renderItemAndEffectIntoGUI(piece, tXR, tY);
	                			RenderUtils.renderItemOverlayIntoGUI(Font.normal.fontRenderer, piece, tXR, tY);
	                			if (tooltip != null && tooltip.size() > 0) {
	                				this.tooltips.add(new GuiTooltip(tXR, tY, 16, 16, tooltip));
	                			}
	                		}
	                	}
	                	tY += 20;
	                }

	                tXL += 20;
	                tXR += 20;
	                tY = y + 70;

	                // draw armor slots (armor stored backwards)
	                for (ItemStack piece : resident.getArmor()) {
	                	if (piece != null && piece != ItemStack.EMPTY) {
	                		List<String> tooltip = piece.getTooltip(null, TooltipFlags.NORMAL);
	                		if (piece.isItemEnchanted() && tooltip != null && tooltip.size() > 0) {
	                			tooltip.set(0, TextFormatting.AQUA + tooltip.get(0));
	                		}

	                		if (guiPage.isLeftPage()) {
	                			RenderUtils.renderItemAndEffectIntoGUI(piece, tXL, tY);
	                			RenderUtils.renderItemOverlayIntoGUI(Font.normal.fontRenderer, piece, tXL, tY);
	                			if (tooltip != null && tooltip.size() > 0) {
	                				this.tooltips.add(new GuiTooltip(tXL, tY, 16, 16, tooltip));
	                			}
	                		}

	                		if (guiPage.isRightPage()) {
	                			RenderUtils.renderItemAndEffectIntoGUI(piece, tXR, tY);
	                			RenderUtils.renderItemOverlayIntoGUI(Font.normal.fontRenderer, piece, tXR, tY);
	                			if (tooltip != null && tooltip.size() > 0) {
	                				this.tooltips.add(new GuiTooltip(tXR, tY, 16, 16, tooltip));
	                			}
	                		}
	                	}
	                	tY -= 20;
	                }
	                
	                GlStateManager.popMatrix();
        		}                
                
	            String professionLabel = TextUtils.translate("tektopiaBook.residents.profession");
	            String professionText = "";
	        	
	            if (professionLabel != null && professionLabel.trim() != "") {
	            	professionText += getProfessionTypeName(resident.getProfessionType());
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(professionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(professionText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(professionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(professionText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String levelLabel = TextUtils.translate("tektopiaBook.residents.level");
	            String levelText = "";
	            
	            if (levelLabel != null && levelLabel.trim() != "") {
		            switch (resident.getProfessionType()) {
		            case CHILD:
		            case NITWIT:
		            	break;
		            default:
		            	levelText = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), true, false);
		            	break;
		            }
		            
		            if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(levelLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(levelText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(levelLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(levelText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String daysAliveLabel = TextUtils.translate("tektopiaBook.residents.daysalive");
	            String daysAliveText = "";
	        	
	            if (daysAliveLabel != null && daysAliveLabel.trim() != "") {
	            	if (resident.getDaysAlive() > 0) {
	            		daysAliveText += "" + resident.getDaysAlive();
	            	}
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(daysAliveLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(daysAliveText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(daysAliveLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(daysAliveText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String healthLabel = TextUtils.translate("tektopiaBook.residents.health");
	            String healthText = "";
	        	
	            if (healthLabel != null && healthLabel.trim() != "") {
	            	healthText += formatResidentStatistic(resident.getHealth(), resident.getMaxHealth(), true);
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(healthLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(healthText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);  
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(healthLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(healthText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String hungerLabel = TextUtils.translate("tektopiaBook.residents.hunger");
	            String hungerText = "";
	        	
	            if (hungerLabel != null && hungerLabel.trim() != "") {
	            	hungerText += formatResidentStatistic(resident.getHunger(), resident.getMaxHunger(), true);
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(hungerLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(hungerText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(hungerLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(hungerText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String happinessLabel = TextUtils.translate("tektopiaBook.residents.happiness");
	            String happinessText = "";
	        	
	            if (happinessLabel != null && happinessLabel.trim() != "") {
	            	happinessText += formatResidentStatistic(resident.getHappy(), resident.getMaxHappy(), true);
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(happinessLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(happinessText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(happinessLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(happinessText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String intelligenceLabel = TextUtils.translate("tektopiaBook.residents.intelligence");
	            String intelligenceText = "";
	        	
	            if (intelligenceLabel != null && intelligenceLabel.trim() != "") {
	            	intelligenceText += formatResidentStatistic(resident.getIntelligence(), resident.getMaxIntelligence(), true);
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(intelligenceLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(intelligenceText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(intelligenceLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(intelligenceText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String blessedLabel = TextUtils.translate("tektopiaBook.residents.blessed");
	            String blessedText = "";
	        	
	            if (blessedLabel != null && blessedLabel.trim() != "") {
	            	blessedText += resident.isBlessed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS;
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(blessedLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printLeft(blessedText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(blessedLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(blessedText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
	            	}
	            }
            	
	            y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            
	            String totalArmorLabel = TextUtils.translate("tektopiaBook.residents.totalarmorvalue");
	            String totalArmorText = "";
	        	
	            if (totalArmorLabel != null && totalArmorLabel.trim() != "") {
	            	totalArmorText += "" + resident.getTotalArmorValue();
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(totalArmorLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(totalArmorText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(totalArmorLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(totalArmorText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
	            	}
	            }
	                        	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            y += 5;

	            HomeData homeData = null;
            	if (resident.hasBed() && this.villageData.getHomesData() != null) {
            		homeData = this.villageData.getHomesData().getHomeByBedPosition(resident.getBedPosition());
            	}
            	
	            String homeStructureLabel = TextUtils.translate("tektopiaBook.residents.home");
	            String homeStructureText = "";
	            
	            if (homeStructureLabel != null && homeStructureLabel.trim() != "") {
            		if (homeData != null) {
            			homeStructureText += " " + homeData.getStructureTypeName() + " (" + formatBlockPos(homeData.getFramePosition()) + ")";
            		} else {
            			homeStructureText += " " + TextUtils.SYMBOL_REDCROSS;
            		}
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(homeStructureLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(homeStructureText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(homeStructureLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(homeStructureText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	        	
	            String bedPositionLabel = TextUtils.translate("tektopiaBook.residents.bed");
	            String bedPositionText = "";
	        	
	            if (bedPositionLabel != null && bedPositionLabel.trim() != "") {
	            	if (resident.hasBed()) {
	            		bedPositionText += " " + formatBlockPos(resident.getBedPosition());
	            	} else {
	            		bedPositionText += " " + TextUtils.SYMBOL_REDCROSS;
	            	}
	            
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(bedPositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(bedPositionText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(bedPositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(bedPositionText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String currentStructureLabel = TextUtils.translate("tektopiaBook.residents.structure");
	            String currentStructureText = "";
	        	 
	            if (currentStructureLabel != null && currentStructureLabel.trim() != "") {
	            	if (structuresData != null && resident.getCurrentStructure() != null) {
	            		StructureData structureData = structuresData.getStructureByFramePosition(resident.getCurrentStructure());
	            		if (structureData != null) {
	            			currentStructureText += " " + structureData.getStructureTypeName() + " (" + formatBlockPos(resident.getCurrentStructure()) + ")";
	            		}
	            	} else {
	            		
		            	if (homeData != null && resident.getCurrentPosition() != null && resident.getBedPosition() != null) {
		            		BlockPos positionCurrent = resident.getCurrentPosition();
		            		BlockPos positionCurrent2 = positionCurrent.down(1);
		            		BlockPos positionBed = resident.getBedPosition();
		            		
		            		if (positionCurrent.equals(positionBed) || positionCurrent2.equals(positionBed)) {
		            			currentStructureText += " " + homeData.getStructureTypeName() + " (" + formatBlockPos(homeData.getFramePosition()) + ")";
		            		}
		            	}
	            	}
	            
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(currentStructureLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(currentStructureText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(currentStructureLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(currentStructureText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            String currentPositionLabel = TextUtils.translate("tektopiaBook.residents.position");
	            String currentPositionText = "";
	        	
	            if (currentPositionLabel != null && currentPositionLabel.trim() != "") {
	            	currentPositionText += " " + formatBlockPos(resident.getCurrentPosition());
	            
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(currentPositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(currentPositionText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y);
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(currentPositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printLeft(currentPositionText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
	            	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            }
	            
	            y += 10;

                String additionalProfessions = TextUtils.translate("tektopiaBook.residents.additionalprofessions");
            	
                if (additionalProfessions != null && additionalProfessions.trim() != "") {
                	additionalProfessions = TextFormatting.DARK_BLUE + additionalProfessions;
                	
        			if (guiPage.isLeftPage()) {
                        Font.small.printLeft(additionalProfessions, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printLeft(additionalProfessions, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
                	}
                	
                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                }
            	
            	String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
            	String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");
            	
    			if (guiPage.isLeftPage()) {
                    Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                	Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                	Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
            	}

            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                
                int index = 1;
                for (Entry<ProfessionType, Integer> additionalProfessionType : resident.getAdditionalProfessions().entrySet()) {
                	if (index > ADDITIONALPROFESSIONS_PER_PAGE) {
                		// only display the top additional professions
                		break;
                	}
                	
                	String additionalProfessionText = getProfessionTypeName(additionalProfessionType.getKey());
                	String additionalProfessionLevel = formatResidentLevel(additionalProfessionType.getValue(), additionalProfessionType.getValue(), false, false);
                	
        			if (guiPage.isLeftPage()) {
                    	Font.small.printLeft(additionalProfessionText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                    	Font.small.printRight(additionalProfessionLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
                    }
	            	
	            	if (guiPage.isRightPage()) {
                    	Font.small.printLeft(additionalProfessionText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    	Font.small.printRight(additionalProfessionLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
                    }

	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
	            	index++;
                }
            }
        }
    }
    
    private void drawPageStatistics(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 

    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getStatisticsPageKeyParts(guiPage.getDataKey());
    	ResidentsData residentsData = this.villageData.getResidentsData();
        String continued = TextUtils.translate("tektopiaBook.continued");

    	Map<Integer, List<ResidentData>> happinessMap = residentsData == null ? null : residentsData.getResidentHappinessStatistics();
		Map<Integer, List<ResidentData>> hungerMap = residentsData == null ? null : residentsData.getResidentHungerStatistics();

        if (dataKey[0].equals("")) {
        	// statistics summary
        	
        	String happinessHeader = TextUtils.translate("tektopiaBook.statistics.happiness");
        	
            if (happinessHeader != null && happinessHeader.trim() != "") {
            	happinessHeader = TextFormatting.DARK_BLUE + happinessHeader;
            	
            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(happinessHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(happinessHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            	
                if (happinessMap != null) {
                	final int[] maxLength = { 0 , 0 };
                	happinessMap.entrySet().forEach(e -> {
                		int rangeEnd = e.getKey();
                		String rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
            			maxLength[0] = Math.max(maxLength[0], Font.small.getStringWidth(rangeName));
                	});
                	maxLength[0] += LABEL_TRAILINGSPACE_X;
                	
                	String rangeHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.range");
                	String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.residents");
                	
                	maxLength[1] += Font.small.getStringWidth(residentsHeader);
                	
        			if (guiPage.isLeftPage()) {
                        Font.small.printRight(rangeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printRight(rangeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
                	}

                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
                	for (Entry<Integer, List<ResidentData>> happinessEntry : happinessMap.entrySet()) {
                		String rangeName = "";
                		String rangeValue = "";
                		
                		int rangeEnd = happinessEntry.getKey();
                		rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
                		rangeValue = "" + happinessEntry.getValue().size();
                		
                    	if (guiPage.isLeftPage()) {
                            Font.small.printRight(rangeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                            Font.small.printRight(rangeValue, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                            Font.small.printRight(rangeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                            Font.small.printRight(rangeValue, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y); 
                    	}
                    	
                    	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	}
                }
                
                y += 10;
            }
            
        	String hungerHeader = TextUtils.translate("tektopiaBook.statistics.hunger");
        	
            if (hungerHeader != null && hungerHeader.trim() != "") {
            	hungerHeader = TextFormatting.DARK_BLUE + hungerHeader;
            	
            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(hungerHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(hungerHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            	
                if (hungerMap != null) {
                	final int[] maxLength = { 0 , 0 };
                	hungerMap.entrySet().forEach(e -> {
                		int rangeEnd = e.getKey();
                		String rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
            			maxLength[0] = Math.max(maxLength[0], Font.small.getStringWidth(rangeName));
                	});
                	maxLength[0] += LABEL_TRAILINGSPACE_X;
                	
                	String rangeHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.range");
                	String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.residents");
                	
                	maxLength[1] += Font.small.getStringWidth(residentsHeader);
                	
        			if (guiPage.isLeftPage()) {
                        Font.small.printRight(rangeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printRight(rangeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                    	Font.small.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
                	}

                	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	
                	for (Entry<Integer, List<ResidentData>> hungerEntry : hungerMap.entrySet()) {
                		String rangeName = "";
                		String rangeValue = "";

                		int rangeEnd = hungerEntry.getKey();
                		rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
                		rangeValue = "" + hungerEntry.getValue().size();
                		
                    	if (guiPage.isLeftPage()) {
                            Font.small.printRight(rangeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
                            Font.small.printRight(rangeValue, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 
                    	}
                    	
                    	if (guiPage.isRightPage()) {
                            Font.small.printRight(rangeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                            Font.small.printRight(rangeValue, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y); 
                    	}
                    	
                    	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
                	}
                }
                
                y += 10;
            }
            
        } else {
        	// statistics details
        	
        	Entry<Integer, List<ResidentData>> statisticsSet = null;
        	String header = "";

			switch (dataKey[0]) {
			case "happiness":
            	header = TextUtils.translate("tektopiaBook.statistics.happinessheader");
            	statisticsSet = happinessMap.entrySet().stream()
                		.filter(e -> dataKey[1].equals(e.getKey().toString()))
                		.findFirst().orElse(null);
				break;
			case "hunger":
            	header = TextUtils.translate("tektopiaBook.statistics.hungerheader");
                statisticsSet = hungerMap.entrySet().stream()
                		.filter(e -> dataKey[1].equals(e.getKey().toString()))
                		.findFirst().orElse(null); 
				break;
			default:
    			break;
			}
        	
            if (header != null && header.trim() != "") {
            	if (!(dataKey[1].equals("0") && dataKey[2].equals("0")) && continued != null && continued.trim() != "") {
            		header += " " + continued;
            	}
            	header = TextFormatting.DARK_BLUE + header;
            	
            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
        	String rangeLabel = TextUtils.translate("tektopiaBook.statistics.range");
        	String rangeText = "";
        	
            if (rangeLabel != null && rangeLabel.trim() != "") {
            	if (statisticsSet != null) {
            		int rangeEnd = statisticsSet.getKey();
            		rangeText += formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
            		
            		if (!dataKey[2].equals("0") && continued != null && continued.trim() != "") {
            			rangeText += " " + continued;
            		}
            	}
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(rangeLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(rangeText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(rangeLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(rangeText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            if (dataKey[2].equals("0")) {
	        	String residentsLabel = TextUtils.translate("tektopiaBook.statistics.total");
	        	String residentsText = "";
	        	
	            if (residentsLabel != null && residentsLabel.trim() != "") {
	            	if (statisticsSet != null && statisticsSet.getValue() != null) {
	            		residentsText += "" + statisticsSet.getValue().size();
	            	}
	            	
	            	if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);  
	            	}
	            }
            }
            
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            y += 10;
            
        	String residentsHeader = TextUtils.translate("tektopiaBook.statistics.residents");
        	
            if (residentsHeader != null && residentsHeader.trim() != "") {
            	residentsHeader = TextFormatting.DARK_BLUE + residentsHeader;
            	
            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            if (statisticsSet != null) {
            	final int[] maxLength = { 0 };
            	maxLength[0] += LABEL_TRAILINGSPACE_X;
            	
    	    	int page = 0;
    	    	try {
    	    		page = Integer.parseInt(dataKey[2]);
    	    	}
    	    	catch (NumberFormatException e) {
    	    		page = 0;
    	    	}
    	    	int startIndex = page * STATRESIDENTS_PER_PAGE;
    	    	int index = 0;

            	String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
            	String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
            	String professionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.profession");
            	String levelHeader = "";
            	
    			switch (dataKey[0]) {
    			case "happiness":
    				levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.happiness");
    				break;
    			case "hunger":
    				levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.hunger");
    				break;
    			default:
        			break;
    			}
            	
            	if (guiPage.isLeftPage()) {
                	Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
                    Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    Font.small.printLeft(professionHeader, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	if (guiPage.isRightPage()) {
                	Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
                    Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    Font.small.printLeft(professionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
    				Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
    	    	
    	    	for (ResidentData resident : statisticsSet.getValue()) {
    	    		if (index >= startIndex && index < startIndex + STATRESIDENTS_PER_PAGE) {
    	    			String residentBed = "";
                		String residentName = "";
                		String residentProfession = "";
                		String residentLevel = "";

                		residentBed = (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
                		residentName = formatResidentName(resident.isMale(), resident.getResidentName(), true);
        				residentProfession += TextUtils.translate("entity." + resident.getProfessionType().name + ".name");
        				
            			switch (dataKey[0]) {
            			case "happiness":
            				residentLevel = "" + formatResidentStatistic(resident.getHappy(), resident.getMaxHappy(), false);
            				break;
            			case "hunger":
            				residentLevel = "" + formatResidentStatistic(resident.getHunger(), resident.getMaxHunger(), false);
            				break;
            			default:
                			break;
            			}

            			if (guiPage.isLeftPage()) {
	                    	Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
	                    	Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
	                    	Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
	                    }
    	            	
    	            	if (guiPage.isRightPage()) {
	                    	Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
	                    	Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
	                    	Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
	                    	Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
	                    }

    	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	    		}
    	    		index++;
    	    	}
            }
        }
    }
    
    private void drawPageStructure(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	StructuresData structuresData = this.villageData.getStructuresData();
		Map<VillageStructureType, Integer> structureTypeCounts = structuresData != null ? structuresData.getStructureTypeCounts() : null;
        String continued = TextUtils.translate("tektopiaBook.continued");
        
        String typeHeader = TextUtils.translate("tektopiaBook.structures.structuretypes");
    	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
    		typeHeader += " " + continued;
    	}
        if (typeHeader != null && typeHeader.trim() != "") {
        	typeHeader = TextFormatting.DARK_BLUE + typeHeader;

        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 
            
        if (structureTypeCounts != null) {
        	final int[] maxLength = { 0 };
        	
        	int page = 0;
        	try {
        		page = Integer.parseInt(dataKey[1]);
        	}
        	catch (NumberFormatException e) {
        		page = 0;
        	}
        	int startIndex = page * LINES_PER_PAGE;
        	int index = 0;
        	
        	String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
        	String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");
        	
        	maxLength[0] += Font.small.getStringWidth(countHeader);
        	
			if (guiPage.isLeftPage()) {
                Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
        	}

        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        	
        	for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
        		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
        			String typeName = getStructureTypeName(structureType);
        			int typeCount = 0;
        			
        			if (structureTypeCounts.containsKey(structureType)) {
        				typeCount = structureTypeCounts.get(structureType);
        			}
        			
        			if (guiPage.isLeftPage()) {
	                    Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X +  maxLength[0], y);
	            	}
	            	
	            	if (guiPage.isRightPage()) {
	                    Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
	                    Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y);
	            	}

	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        		}  
        		index++;
        	}
        }
    }
    
    private void drawPageStructureType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 

    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		VillageStructureType structureType = VillageStructureType.valueOf(dataKey[0]);
        String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");

		StructuresData structuresData = this.villageData.getStructuresData();
		Map<VillageStructureType, Integer> structureTypeCounts = structuresData != null ? structuresData.getStructureTypeCounts() : null;
		List<StructureData> structures = structuresData != null ? structuresData.getStructuresByType(structureType) : null;
				
		String typeName = getStructureTypeName(structureType);
        
        if (typeName != null && typeName.trim() != "") {
        	typeName += " " + summary;
        	if (!dataKey[1].equals("0") && continued != null && continued.trim() != "") {
        		typeName += " " + continued;
        	}
        	typeName = TextFormatting.DARK_BLUE + typeName;
        	
        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

        if (dataKey[1].equals("0")) {
        	
            String totalLabel = TextUtils.translate("tektopiaBook.structuretypes.total");
            String totalText = "";

            if (totalLabel != null && totalLabel.trim() != "") {
            	if (structureTypeCounts != null && structureTypeCounts.containsKey(structureType)) {
            		totalText += "" + structureTypeCounts.get(structureType);
            	}
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            }
        } 
    	
    	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

    	String header = TextUtils.translate("tektopiaBook.structuretypes.frameposition");
        
        if (header != null && header.trim() != "") {
        	header = TextFormatting.DARK_BLUE + header;

        	if (guiPage.isLeftPage()) {
                Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

		if (structures != null) {
        	final int[] maxLength = { 0 , 0 };
        	maxLength[0] += LABEL_TRAILINGSPACE_X;

        	int page = 0;
        	try {
        		page = Integer.parseInt(dataKey[1]);
        	}
        	catch (NumberFormatException e) {
        		page = 0;
        	}
        	int startIndex = page * LINES_PER_PAGE;
        	int index = 0;

        	String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
        	String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
        	String floorTilesHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.structuretypes.floor");
        	String validTextHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.valid");
        	
        	maxLength[1] += Font.small.getStringWidth(floorTilesHeader);
        	
        	if (guiPage.isLeftPage()) {
        		Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
            	Font.small.printLeft(floorTilesHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
        	}

        	if (guiPage.isRightPage()) {
                Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
            	Font.small.printLeft(floorTilesHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
        	
        	for (StructureData structure : structures) {
        		if (index >= startIndex && index < startIndex + LINES_PER_PAGE) {
        			
        			String indexName = "" + (index + 1);
        			String framePosition = formatBlockPos(structure.getFramePosition());
        			if (this.villageData.getFramePosition() != null && structure.getFramePosition() != null && this.villageData.getFramePosition().equals(structure.getFramePosition())) {
        				framePosition = TextFormatting.UNDERLINE + framePosition;
        			}
        			String floorTiles = "" + (structure.getFloorTileCount() >= 0 ? structure.getFloorTileCount() : "0");
        			String validText = "" + (structure.isValid() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
        			
                	if (guiPage.isLeftPage()) {
	                    Font.small.printRight(indexName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printLeft(framePosition, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    	Font.small.printRight(floorTiles, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
        				Font.small.printRight(validText, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
                	}
                	
                	if (guiPage.isRightPage()) {
                        Font.small.printRight(indexName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                        Font.small.printLeft(framePosition, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
                    	Font.small.printRight(floorTiles, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
        				Font.small.printRight(validText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
                	}
                	
	            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
        		}
        		index++;
    		}
		}
    }
    
    private void drawPageSummary(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

    	String[] dataKey = getPageKeyParts(guiPage.getDataKey());
    	String summaryName = "";
    	String summaryInformation = "";
    	String dataName = "";
    	List<String> dataInformation = new ArrayList<String>();
    	
    	EconomyData economyData;
    	HomesData homesData;
    	ResidentsData residentsData;
		StructuresData structuresData;
   	
    	switch (dataKey[0]) {
    	case BOOKMARK_KEY_ECONOMY:
        	summaryName = TextUtils.translate("bookmark.economy.name");
    		summaryInformation = TextUtils.translate("bookmark.economy.information");    
			dataName = TextUtils.translate("tektopiaBook.summary.header");
    		
        	economyData = this.villageData.getEconomyData();

    		if (economyData != null) {
        		String merchantSales = TextUtils.translate("tektopiaBook.economy.merchantsales");
        		
        		if (!StringUtils.isNullOrWhitespace(merchantSales)) {
        			dataInformation.add(merchantSales + "|" + economyData.getMerchantSales() + "|");
        		}
    		}
    		break;
    	case BOOKMARK_KEY_HOMES:
        	summaryName = TextUtils.translate("bookmark.homes.name");
    		summaryInformation = TextUtils.translate("bookmark.homes.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");
    		
        	homesData = this.villageData.getHomesData();
        	residentsData = this.villageData.getResidentsData();

        	if (homesData != null) {
        		String homesTotal = TextUtils.translate("tektopiaBook.homes.totalhomes");
        		
        		if (!StringUtils.isNullOrWhitespace(homesTotal)) {
        			dataInformation.add(homesTotal + "|" + homesData.getHomesCount() + "|");
        		}

        		String homesTotalBeds = TextUtils.translate("tektopiaBook.homes.totalbeds");
        		
        		if (!StringUtils.isNullOrWhitespace(homesTotalBeds)) {
        			dataInformation.add(homesTotalBeds + "|" + homesData.getTotalBeds() + "|");
        		}
    		}

        	if (residentsData != null) {
        		String homesTotalResidents = TextUtils.translate("tektopiaBook.homes.totalresidents");
        		
        		if (!StringUtils.isNullOrWhitespace(homesTotalResidents)) {
        			dataInformation.add(homesTotalResidents + "|" + residentsData.getResidentsCount() + "|");
        		}
        	}
    		break;
    	case BOOKMARK_KEY_PROFESSIONS:
        	summaryName = TextUtils.translate("bookmark.professions.name");
    		summaryInformation = TextUtils.translate("bookmark.professions.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

        	residentsData = this.villageData.getResidentsData();

        	if (residentsData != null) {
        		String professionsTotal = TextUtils.translate("tektopiaBook.professions.total");
        		
        		if (!StringUtils.isNullOrWhitespace(professionsTotal)) {
        			dataInformation.add(professionsTotal + "|" + residentsData.getResidentsCount() + "|");
        		}
        	}
    		break;
    	case BOOKMARK_KEY_RESIDENTS:
        	summaryName = TextUtils.translate("bookmark.residents.name");
    		summaryInformation = TextUtils.translate("bookmark.residents.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

        	residentsData = this.villageData.getResidentsData();

        	if (residentsData != null) {
        		String residentsTotal = TextUtils.translate("tektopiaBook.residents.total");
        		
        		if (!StringUtils.isNullOrWhitespace(residentsTotal)) {
        			dataInformation.add(residentsTotal + "|" + residentsData.getResidentsCount() + "|");
        		}
        		
        		String residentsTotalAdults = TextUtils.translate("tektopiaBook.residents.adults");
        		
        		if (!StringUtils.isNullOrWhitespace(residentsTotalAdults)) {
        			dataInformation.add(residentsTotalAdults + "|" + residentsData.getAdultCount() + "|");
        		}
        		
        		String residentsTotalChildren = TextUtils.translate("tektopiaBook.residents.children");
        		
        		if (!StringUtils.isNullOrWhitespace(residentsTotalChildren)) {
        			dataInformation.add(residentsTotalChildren + "|" + residentsData.getChildCount() + "|");
        		}
        		
        		String residentsTotalMales = TextUtils.translate("tektopiaBook.residents.males");
        		
        		if (!StringUtils.isNullOrWhitespace(residentsTotalMales)) {
        			dataInformation.add(residentsTotalMales + "|" + residentsData.getMaleCount() + "|");
        		}
        		
        		String residentsTotalFemales = TextUtils.translate("tektopiaBook.residents.females");
        		
        		if (!StringUtils.isNullOrWhitespace(residentsTotalFemales)) {
        			dataInformation.add(residentsTotalFemales + "|" + residentsData.getFemaleCount() + "|");
        		}
        		
        		String residentsNobedsLabel = TextUtils.translate("tektopiaBook.residents.nobed");
        		String residentsNobedsText = "";
        				
        		if (!StringUtils.isNullOrWhitespace(residentsNobedsLabel)) {
            		if (residentsData.getNoBedCount() > 0)
            			residentsNobedsText += TextFormatting.DARK_RED;
            		residentsNobedsText += residentsData.getNoBedCount();
            		dataInformation.add(residentsNobedsLabel + "|" + residentsNobedsText + "|");
        		}
        	}
    		break;
    	case BOOKMARK_KEY_STATISTICS:
        	summaryName = TextUtils.translate("bookmark.statistics.name");
    		summaryInformation = TextUtils.translate("bookmark.statistics.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

        	residentsData = this.villageData.getResidentsData();

        	if (residentsData != null) {
        		String statisticsTotal = TextUtils.translate("tektopiaBook.statistics.total");
        		
        		if (!StringUtils.isNullOrWhitespace(statisticsTotal)) {
        			dataInformation.add(statisticsTotal + "|" + residentsData.getResidentsCount() + "|");
        		}
        	}
    		break;
    	case BOOKMARK_KEY_STRUCTURES:
    		summaryName = TextUtils.translate("bookmark.structures.name");
    		summaryInformation = TextUtils.translate("bookmark.structures.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

    		structuresData = this.villageData.getStructuresData();

        	if (structuresData != null) {
    			String structuresTotal = TextUtils.translate("tektopiaBook.structures.total");
        		
        		if (!StringUtils.isNullOrWhitespace(structuresTotal)) {
        			dataInformation.add(structuresTotal + "|" + structuresData.getStructuresCount() + "|");
        		}
        	}
    		break;
    	case BOOKMARK_KEY_VILLAGE:
    		summaryName = TextUtils.translate("bookmark.village.name");
    		summaryInformation = TextUtils.translate("bookmark.village.information");
    		break;
    	}
    	
    	y += 50;
    	
    	if (summaryName != null && summaryName.trim() != "") {
    		summaryName = TextFormatting.DARK_BLUE + summaryName;
        	
        	if (guiPage.isLeftPage()) {
                Font.normal.printCentered(summaryName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printCentered(summaryName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	}
    	
    	y += 20;
    	
    	if (summaryInformation != null && summaryInformation.trim() != "") {
        	if (guiPage.isLeftPage()) {
            	List<String> textLines = StringUtils.split(summaryInformation, PAGE_LEFTPAGE_WIDTH, Font.small.fontRenderer);

            	for (int lineIndex = textLines.size() - 1; lineIndex >= 0; lineIndex--) {
            		Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_LEFTPAGE_CENTER_X, y);
            		
            		y -= Font.small.fontRenderer.FONT_HEIGHT;
            	}
        	}
        	
        	if (guiPage.isRightPage()) {
            	List<String> textLines = StringUtils.split(summaryInformation, PAGE_RIGHTPAGE_WIDTH, Font.small.fontRenderer);

            	for (int lineIndex = textLines.size() - 1; lineIndex >= 0; lineIndex--) {
            		Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_RIGHTPAGE_CENTER_X, y);
            		
            		y -= Font.small.fontRenderer.FONT_HEIGHT;
            	}
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	}
    	
    	y = this.y + PAGE_BODY_Y + ((PAGE_FOOTER_Y - PAGE_BODY_Y) / 2);
    	
    	if (dataName != null && dataName.trim() != "") {
    		dataName = TextFormatting.DARK_BLUE + dataName;
        	
        	if (guiPage.isLeftPage()) {
                Font.small.printCentered(dataName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.small.printCentered(dataName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
        	}
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    	}
    	
    	if (dataInformation != null && dataInformation.size() > 0) {
    		for (String dataLine : dataInformation) {
    			if (dataLine != null && dataLine.trim() != "") {
    				String[] parts = dataLine.split("[|]");
    				
    				if (guiPage.isLeftPage()) {
    					if (parts.length > 0 && parts[0] != null && parts[0].trim() != "")
    						Font.small.printLeft(parts[0], this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
    					if (parts.length > 1 && parts[1] != null && parts[1].trim() != "")
    						Font.small.printRight(parts[1], this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);  
    					if (parts.length > 2 && parts[2] != null && parts[2].trim() != "")
    						Font.small.printRight(parts[2], this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y); 
    	        	}
    	        	
    	        	if (guiPage.isRightPage()) {
    					if (parts.length > 0 && parts[0] != null && parts[0].trim() != "")
    						Font.small.printLeft(parts[0], this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
    					if (parts.length > 1 && parts[1] != null && parts[1].trim() != "")
    						Font.small.printRight(parts[1], this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
    					if (parts.length > 2 && parts[2] != null && parts[2].trim() != "")
    						Font.small.printRight(parts[2], this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y); 
    	        	}
    	        	
    	        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
    			}
    		}
    	}
    }
    
    private void drawPageVillage(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
        drawHeader(mouseX, mouseY, partialTicks, guiPage);
        drawFooter(mouseX, mouseY, partialTicks, guiPage); 
        
    	int y = this.y + PAGE_BODY_Y;
    	int indentX = 10;

        String header = TextUtils.translate("tektopiaBook.village.header");
        
        if (header != null && header.trim() != "") {
        	header = TextFormatting.DARK_BLUE + header;

        	if (guiPage.isLeftPage()) {
                Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        } 

        if (this.villageData != null) {
            String originLabel = TextUtils.translate("tektopiaBook.village.origin");
            String originText = "";

            if (originLabel != null && originLabel.trim() != "") {
            	originText += formatBlockPos(this.villageData.getVillageOrigin());
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(originLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(originText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(originLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(originText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            y += 10;
            
            String boundaries = TextUtils.translate("tektopiaBook.village.boundaries");

            if (boundaries != null && boundaries.trim() != "") {
            	boundaries = TextFormatting.DARK_BLUE + boundaries;

            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(boundaries, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(boundaries, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            String boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynorthwest");
            String boundaryText = "";

            if (boundaryLabel != null && boundaryLabel.trim() != "") {
            	boundaryText += formatBlockPos(this.villageData.getVillageNorthWestCorner());
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynortheast");
            boundaryText = "";
            
            if (boundaryLabel != null &&boundaryLabel.trim() != "") {
            	boundaryText += formatBlockPos(this.villageData.getVillageNorthEastCorner());
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysouthwest");
            boundaryText = "";

            if (boundaryLabel != null && boundaryLabel.trim() != "") {
            	boundaryText += formatBlockPos(this.villageData.getVillageSouthWestCorner());
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysoutheast");
            boundaryText = "";

            if (boundaryLabel != null && boundaryLabel.trim() != "") {
            	boundaryText += formatBlockPos(this.villageData.getVillageSouthEastCorner());

            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            y += 10;

            String totals = TextUtils.translate("tektopiaBook.village.totals");

            if (totals != null && totals.trim() != "") {
            	totals = TextFormatting.DARK_BLUE + totals;

            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(totals, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(totals, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }

            String structuresLabel = TextUtils.translate("tektopiaBook.village.structures");
            String structuresText = "";

            if (structuresLabel != null && structuresLabel.trim() != "") {
            	if (this.villageData.getStructuresData() != null)
            		structuresText += "" + this.villageData.getStructuresData().getStructuresCount();
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(structuresLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(structuresText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(structuresLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(structuresText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            String residentsLabel = TextUtils.translate("tektopiaBook.village.residents");
            String residentsText = "";

            if (residentsLabel != null && residentsLabel.trim() != "") {
            	if (this.villageData.getResidentsData() != null)
            		residentsText += "" + this.villageData.getResidentsData().getResidentsCount();
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            String professionSalesLabel = TextUtils.translate("tektopiaBook.village.professionsales");
            String professionSalesText = "";

            if (professionSalesLabel != null && professionSalesLabel.trim() != "") {
            	if (this.villageData.getEconomyData() != null)
            		professionSalesText += "" + this.villageData.getEconomyData().getProfessionSales();
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(professionSalesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
                    Font.small.printRight(professionSalesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(professionSalesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(professionSalesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            String merchantSalesLabel = TextUtils.translate("tektopiaBook.village.merchantsales");
            String merchantSalesText = "";

            if (merchantSalesLabel != null && merchantSalesLabel.trim() != "") {
            	if (this.villageData.getEconomyData() != null)
            		merchantSalesText += "" + this.villageData.getEconomyData().getMerchantSales();
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(merchantSalesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(merchantSalesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(merchantSalesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(merchantSalesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
            
            y += 10;

            String statistics = TextUtils.translate("tektopiaBook.village.statistics");

            if (statistics != null && statistics.trim() != "") {
            	statistics = TextFormatting.DARK_BLUE + statistics;

            	if (guiPage.isLeftPage()) {
                    Font.normal.printLeft(statistics, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.normal.printLeft(statistics, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
            	}
            	
            	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }

            String nobedsLabel = TextUtils.translate("tektopiaBook.village.nobed");
            String nobedsText = "";
            
            if (nobedsLabel != null && nobedsLabel.trim() != "") {
            	if (this.villageData.getResidentsData() != null) {
            		int nobedsCount = this.villageData.getResidentsData().getNoBedCount();
            		if (nobedsCount > 0)
            			nobedsText += TextFormatting.DARK_RED;
            		nobedsText += "" + nobedsCount;
            	}
            	
            	if (guiPage.isLeftPage()) {
                    Font.small.printLeft(nobedsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(nobedsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
            	}
            	
            	if (guiPage.isRightPage()) {
                    Font.small.printLeft(nobedsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
                    Font.small.printRight(nobedsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
            	}
            	
            	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
            }
        }
    }
    
    private void drawPageTitle(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		int y = this.y + PAGE_BODY_Y;
		
        String bookName = TextUtils.translate("tektopiaBook.name");
        String bookInformation = TextUtils.translate("tektopiaBook.information");
    	
    	y += 50;
    	
        if (bookName != null && bookName.trim() != "") {
        	bookName = TextFormatting.DARK_RED + bookName;
        	
        	if (guiPage.isLeftPage()) {
                Font.normal.printCentered(bookName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
        	}
        	
        	if (guiPage.isRightPage()) {
                Font.normal.printCentered(bookName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
        	}
        	
        	y += Font.normal.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        }

        if (this.villageData != null) {
			String villageName = this.villageData.getVillageName();
	        String villageText = TextUtils.translate("tektopiaBook.village.name");
			
			if (villageName != null && villageName.trim() != "") {
				if (villageText != null && villageText.trim() != "") {
					villageText += " ";
				}
	    		villageText = TextFormatting.GOLD + "-----< " + villageText + villageName + " >-----";
	    		
	    		if (guiPage.isLeftPage()) {
	    			Font.small.printCentered(villageText, this.x + PAGE_LEFTPAGE_CENTER_X, y);
	    		}
	    		
	    		if (guiPage.isRightPage()) {
	    			Font.small.printCentered(villageText, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
	    		}
			}
			
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
		}

        if (bookInformation != null && bookInformation.trim() != "") {
        	y = (this.y + PAGE_FOOTER_Y) - Font.small.fontRenderer.FONT_HEIGHT;
        	
        	if (guiPage.isLeftPage()) {
            	List<String> textLines = StringUtils.split(bookInformation, PAGE_LEFTPAGE_WIDTH, Font.small.fontRenderer);
            	
            	for (int lineIndex = textLines.size() - 1; lineIndex >= 0; lineIndex--) {
            		Font.small.printLeft(textLines.get(lineIndex), this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
            		
            		y -= Font.small.fontRenderer.FONT_HEIGHT;
            	}
        	}
        	
        	if (guiPage.isRightPage()) {
            	List<String> textLines = StringUtils.split(bookInformation, PAGE_RIGHTPAGE_WIDTH, Font.small.fontRenderer);

            	for (int lineIndex = textLines.size() - 1; lineIndex >= 0; lineIndex--) {
            		Font.small.printLeft(textLines.get(lineIndex), this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
            		
            		y -= Font.small.fontRenderer.FONT_HEIGHT;
            	}
            }
        	
        	y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
        }
    }
    
    private void actionPerformed(GuiBookmark bookmark) {
    	if (bookmark != null) {
    		// set the page to the bookmark page
    		setLeftPageIndex(bookmark.getPageIndex());
    		
    		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
    	}
    }
    
    private void actionPerformed(GuiButton button) {
    	if (button != null) {
    		switch (button.getKey()) {
    		case BUTTON_KEY_PREVIOUSPAGE:
    			setLeftPageIndex(this.leftPageIndex - 2);
        		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
    			break;
    		case BUTTON_KEY_NEXTPAGE:
    			setLeftPageIndex(this.leftPageIndex + 2);
        		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
    			break;
    		case BUTTON_KEY_STARTBOOK:
    			setLeftPageIndex(0);
        		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
    			break;
    		case BUTTON_KEY_ENDBOOK:
    			setLeftPageIndex(this.pages.size());
        		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
    			break;
    		default:
    			break;
    		} 
		}   
	}
    
    private String formatBlockPos(BlockPos blockPos) {
    	if (blockPos == null) {
    		return "";
    	}
    	
    	return blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ();
    }

    private String formatResidentLevel(int level, int baseLevel, boolean showBaseLevel, boolean showPrefix) {
    	String residentLevel = "";
    	if (level > 0) {
    		if (showPrefix) {
				residentLevel += TextUtils.translate("tektopiaBook.residents.level") + " ";
    		}
    		
	    	if (level > baseLevel) {
				residentLevel += "" + TextFormatting.DARK_GREEN + level + TextFormatting.RESET + (showBaseLevel ? " (" + baseLevel + ")" : "");   		
	    	} else {
				residentLevel += "" + level;
	    	}
    	}
    	return residentLevel;
    }
    
    private String formatResidentName(Boolean isMale, String name, Boolean includeGenderSymbol) {
    	String residentName = (isMale ? TextFormatting.BLUE : TextFormatting.LIGHT_PURPLE) + stripTextFormatting(name);
		if (includeGenderSymbol)
			residentName += " " + (isMale ? TextUtils.SYMBOL_MALE : TextUtils.SYMBOL_FEMALE);
    	return residentName;
    }
    
    private String formatResidentStatistic(int value, int maxValue, boolean showMaxLevel) {
    	int percentage = (100 / maxValue) * value;
    	if (percentage <= 10) {
    		return "" + TextFormatting.DARK_RED + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    	else if (percentage <= 50) {
    		return "" + TextFormatting.GOLD + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    	else {
    		return "" + TextFormatting.DARK_GREEN + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    }
    
    private String formatResidentStatistic(float value, float maxValue, boolean showMaxLevel) {
    	float percentage = (100 / maxValue) * value;
    	if (percentage <= 10) {
    		return "" + TextFormatting.DARK_RED + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    	else if (percentage <= 50) {
    		return "" + TextFormatting.GOLD + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    	else {
    		return "" + TextFormatting.DARK_GREEN + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
    	}
    }
    
    private String formatStatisticsRange(int lowValue, int highValue, int maxValue) {
    	float percentage = (100 / maxValue) * highValue;
    	if (percentage <= 10) {
    		return "" + TextFormatting.DARK_RED + lowValue + (lowValue != highValue ? " - " + highValue : "");
    	}
    	else if (percentage <= 50) {
    		return "" + TextFormatting.GOLD + lowValue + (lowValue != highValue ? " - " + highValue : "");
    	}
    	else {
    		return "" + TextFormatting.DARK_GREEN + lowValue + (lowValue != highValue ? " - " + highValue : "");
    	}
    }
    
    private String getPageKey(String pageKey, int pageNumber) {
    	if (pageKey == null)
    		pageKey = "";
    	
    	return pageKey + "@" + pageNumber;
    }
    
    private String[] getPageKeyParts(String pageKey) {
    	String[] result = new String[] { "", "0" };
    	
    	if (pageKey != null && pageKey.trim() != "") {
    		if (pageKey.contains("@")) {
    			String[] parts = pageKey.split("@");
    			if (parts.length > 0)
    				result[0] = parts[0];
    			if (parts.length > 1)
    				result[1] = parts[1];
			} else {
    			result[0] = pageKey;
    		}
    	}
    	
    	return result;
    }

    private String getProfessionTypeName(ProfessionType professionType) {
    	if (professionType == null) {
    		return "";
    	}
    	
    	return TextUtils.translate("entity." + professionType.name + ".name");
    }
    
    private String getStatisticsPageKey(String pageKey, int rangekey, int pageNumber) {
    	if (pageKey == null)
    		pageKey = "";
    	
    	return pageKey + "@" + rangekey + "@" + pageNumber;
    }
    
    private String[] getStatisticsPageKeyParts(String pageKey) {
    	String[] result = new String[] { "", "0", "0" };
    	
    	if (pageKey != null && pageKey.trim() != "") {
    		if (pageKey.contains("@")) {
    			String[] parts = pageKey.split("@");
    			if (parts.length > 0)
    				result[0] = parts[0];
    			if (parts.length > 1)
    				result[1] = parts[1];
    			if (parts.length > 2)
    				result[2] = parts[2];
			} else {
    			result[0] = pageKey;
    		}
    	}
    	
    	return result;
    }
    
    private String getStructureTypeName(VillageStructureType structureType) {
    	if (structureType == null || structureType.itemStack == null) {
    		return "";
    	}
    	
    	return structureType.itemStack.getDisplayName();
    }
    
    private void setLeftPageIndex(int pageIndex) {
    	if (pageIndex < 0)
    		pageIndex = 1;
    	if (pageIndex >= this.pages.size())
    		pageIndex = this.pages.size() - 1;
    	
    	boolean isEven = pageIndex % 2 == 0;
    	
		this.leftPageIndex = isEven ? pageIndex : pageIndex - 1;    	
    }
    
    private String stripTextFormatting(String value) {
    	if (value == null || value.trim() == "") {
    		return "";
    	}
    	
    	return TextFormatting.getTextWithoutFormattingCodes(value);
    }
    
}
