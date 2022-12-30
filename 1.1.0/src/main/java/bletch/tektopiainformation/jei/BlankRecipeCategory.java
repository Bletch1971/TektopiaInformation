package bletch.tektopiainformation.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModDetails;

@ParametersAreNonnullByDefault
public abstract class BlankRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {
	
	private final String category;
	private final String title;
    private final IDrawable icon;
    private final IDrawable background;

    protected BlankRecipeCategory(String category, IDrawable icon, IDrawable background) {
    	this.category = category;
    	this.title = TextUtils.translate("jei." + category + ".title");
        this.icon = icon;
        this.background = background;
    }

    @Override
	public String getUid() {
    	return TektopiaJei.getJeiUid(this.category);
	}
	
    @Override
    public String getModName() {
        return ModDetails.MOD_NAME;
    }	
    
	public String getCategory() {
		return this.category;
	}
	
	@Override
	public String getTitle() {
		return this.title;
	}

    @Nullable
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }
	
	@Override
	public IDrawable getBackground() {
		return this.background;
	}
	
}
