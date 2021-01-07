package bletch.tektopiainformation.core;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid=ModDetails.MOD_ID, category="")
@ParametersAreNonnullByDefault
public class ModConfig {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		
		if (event.getModID().equals(ModDetails.MOD_ID)) {
			ConfigManager.sync(ModDetails.MOD_ID, Type.INSTANCE);
		}
		
	}
	
	@Config.LangKey("config.debug")
	public static final Debug debug = new Debug();
	
	@Config.LangKey("config.tooltips")
	public static final Tooltips tooltips = new Tooltips();
	
	@Config.LangKey("config.jei")
	public static final Jei jei = new Jei();
	
	@Config.LangKey("config.waila")
	public static final Waila waila = new Waila();
	
	@Config.LangKey("config.top")
	public static final TheOneProbe top = new TheOneProbe();
	
	@Config.LangKey("config.gui")
	public static final GuiSettings gui = new GuiSettings();
	
	public static class Debug {
		
		@Config.Comment("If true, debug information will be output to the console.")
		@Config.LangKey("config.debug.enableDebug")
		public boolean enableDebug = false;		
	
		@Config.Comment("If true, the item translation key will be show in the tooltip.")
		@Config.LangKey("config.debug.showTooltipTranslationKey")
		public boolean showTooltipTranslationKey = false;	
		
		@Config.Comment("If true, any blocks/items that are missing an Information record will be output to the console.")
		@Config.LangKey("config.debug.showJeiBlocksMissingInformation")
		@Config.RequiresMcRestart
		public boolean showJeiMissingInformation = false;			
		
		@Config.Comment("If true, any blocks/items that register an Information record will be output to the console.")
		@Config.LangKey("config.debug.showJeiBlocksRegisteredInformation")
		@Config.RequiresMcRestart
		public boolean showJeiRegisteredInformation = false;
		
		@Config.Comment("If true, any blocks/items that are missing an Information record will be registered.")
		@Config.LangKey("config.debug.registerJeiBlocksMissingInformation")
		@Config.RequiresMcRestart
		public boolean registerJeiMissingInformation = false;		
		
		@Config.Comment("If true, the block translation key will be show in The One Probe.")
		@Config.LangKey("config.debug.showTopBlockTranslationKey")
		public boolean showTopBlockTranslationKey = false;	
		
		@Config.Comment("If true, any blocks that are registered will be output to the console.")
		@Config.LangKey("config.debug.showWailaBlocksRegistered")
		@Config.RequiresMcRestart
		public boolean showWailaBlocksRegistered = false;	
		
		@Config.Comment("If true, the block translation key will be show in Waila.")
		@Config.LangKey("config.debug.showWailaBlockTranslationKey")
		public boolean showWailaBlockTranslationKey = false;	
		
		@Config.Comment("If true, any entities that are registered will be output to the console.")
		@Config.LangKey("config.debug.showWailaEntitiesRegistered")
		@Config.RequiresMcRestart
		public boolean showWailaEntitiesRegistered = false;	
		
	}
	
	public static class Tooltips {
		
		@Config.Comment("If true, will integrate with item tooltips.")
		@Config.LangKey("config.tooltips.enableTooltipIntegration")
		@Config.RequiresMcRestart
		public boolean enableTooltipIntegration = true;

		@Config.Comment("If true, will only show tooltip information when Shift key pressed.")
		@Config.LangKey("config.tooltips.useShiftKey")
		public boolean useShiftKey = true;
		
		@Config.Comment("If true, will only show tooltip information when Advanced Tooltips enabled (F3+H).")
		@Config.LangKey("config.tooltips.restrictToAdvancedTooltips")
		public boolean restrictToAdvancedTooltips = false;

		@Config.Comment("If true, will show the hold shift key for more information.")
		@Config.LangKey("config.tooltips.showShiftKeyInfo")
		public boolean showShiftKeyInfo = true;
		
	}

	public static class Jei {
		
		@Config.Comment("If true, will integrate with JEI.")
		@Config.LangKey("config.jei.enableJeiIntegration")
		@Config.RequiresMcRestart
		public boolean enableJeiIntegration = true;
		
		@Config.Comment("If true, will show the information tab in JEI.")
		@Config.LangKey("config.jei.showJeiInformationTab")
		@Config.RequiresMcRestart
		public boolean showJeiInformationTab = true;
		
	}

	public static class Waila {
		
		@Config.LangKey("config.waila.blocks")
		public final Blocks blocks = new Blocks();
		
		@Config.LangKey("config.waila.entities")
		public final Entities entities = new Entities();
		
		@Config.Comment("If true, will integrate with Waila.")
		@Config.LangKey("config.waila.enableWailaIntegration")
		@Config.RequiresMcRestart
		public boolean enableWailaIntegration = true;

		@Config.Comment("If true, will only show information when Sneaking.")
		@Config.LangKey("config.waila.useSneaking")
		public boolean useSneaking = false;

		public static class Blocks {
			
			@Config.Comment("If true, will show the block tooltip in Waila.")
			@Config.LangKey("config.waila.blocks.showBlockTooltip")
			public boolean showBlockTooltip = true;	
			
			@Config.Comment("If true, will show the block information in Waila.")
			@Config.LangKey("config.waila.blocks.showBlockInformation")
			public boolean showBlockInformation = true;			
			
		}

		public static class Entities {
		
			@Config.Comment("If true, will show the entity information in Waila.")
			@Config.LangKey("config.waila.entities.showEntityInformation")
			public boolean showEntityInformation = true;	
			
		}

	}

	public static class TheOneProbe {
		
		@Config.LangKey("config.top.blocks")
		public final Blocks blocks = new Blocks();
		
		@Config.LangKey("config.top.entities")
		public final Entities entities = new Entities();
		
		@Config.Comment("If true, will integrate with The One Probe.")
		@Config.LangKey("config.top.enableTopIntegration")
		@Config.RequiresMcRestart
		public boolean enableTopIntegration = true;

		@Config.Comment("If true, will only show information when Sneaking.")
		@Config.LangKey("config.top.useSneaking")
		public boolean useSneaking = true;
		
		public static class Blocks {	
			
			@Config.Comment("If true, will show the block tooltip in The One Probe.")
			@Config.LangKey("config.top.blocks.showBlockTooltip")
			public boolean showBlockTooltip = true;	
			
			@Config.Comment("If true, will show the block information in The One Probe.")
			@Config.LangKey("config.top.blocks.showBlockInformation")
			public boolean showBlockInformation = true;				

		}

		public static class Entities {
		
			@Config.Comment("If true, will show the entity information in The One Probe.")
			@Config.LangKey("config.top.entities.showEntityInformation")
			public boolean showEntityInformation = true;
			
		}
		
	}

	public static class GuiSettings {
		
		@Config.LangKey("config.gui.tektopiaInformationBook")
		public final TektopiaInformationBook tektopiaInformationBook = new TektopiaInformationBook();
		
		@Config.Comment("If true, will integrate with the GUI.")
		@Config.LangKey("config.gui.enableGuiIntegration")
		@Config.RequiresMcRestart
		public boolean enableGuiIntegration = true;

		public static class TektopiaInformationBook {
			
			@Config.Comment("If true, will show the TekTopia Information Book when the player right-clicks a villager with a book in hand. When connected to a server, both the client and server must have this option enabled to display to information book.")
			@Config.LangKey("config.gui.tektopiaInformationBook.enableTektopiaInformationBook")
			public boolean enableTektopiaInformationBook = true;
			
		}
		
	}
	
}
