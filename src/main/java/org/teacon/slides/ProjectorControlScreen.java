package org.teacon.slides;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public final class ProjectorControlScreen extends ContainerScreen<ProjectorControlContainer>
{

    private static final ResourceLocation GUI_BG = new ResourceLocation("slide_show", "textures/gui/projector.png");

    private TextFieldWidget urlInput;
    private TextFieldWidget colorInput;
    private TextFieldWidget widthInput;
    private TextFieldWidget heightInput;
    private TextFieldWidget offsetXInput;
    private TextFieldWidget offsetYInput;
    private TextFieldWidget offsetZInput;

    private String url = "";
    private int color = 0;
    private float width = 0F, height = 0F, offsetX = 0F, offsetY = 0F, offsetZ = 0F;
    private boolean invalidURL = false, invalidColor = false, invalidWidth = false, invalidHeight = false,
            invalidOffsetX = false, invalidOffsetY = false, invalidOffsetZ = false;

    protected ProjectorControlScreen(ProjectorControlContainer container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
    }

    @Override
    protected void init()
    {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.urlInput = new TextFieldWidget(this.font, this.guiLeft + 10, this.guiTop + 20, 120, 16, new StringTextComponent("Image URL"));
        this.urlInput.setMaxStringLength(32767);
        this.urlInput.setResponder(input ->
        {
            try
            {
                new java.net.URL(this.url = input);
                this.invalidURL = false;
            }
            catch (Exception e)
            {
                this.invalidURL = true;
            }
        });
        this.urlInput.setText(this.container.currentSlide.imageLocation);
        this.urlInput.setVisible(true);
        this.children.add(this.urlInput);
        this.colorInput = new TextFieldWidget(this.font, this.guiLeft + 10, this.guiTop + 55, 60, 16, new StringTextComponent("Color"));
        this.colorInput.setMaxStringLength(8);
        this.colorInput.setResponder(input ->
        {
            try
            {
                this.color = Integer.parseUnsignedInt(input, 16);
                this.invalidColor = false;
            }
            catch (Exception e)
            {
                this.invalidColor = true;
            }
        });
        this.colorInput.setText(Integer.toUnsignedString(this.container.currentSlide.color, 16));
        this.colorInput.setVisible(true);
        this.children.add(this.colorInput);

        this.widthInput = new TextFieldWidget(this.font, this.guiLeft + 10, this.guiTop + 90, 40, 16, new StringTextComponent("Width"));
        this.widthInput.setResponder(input ->
        {
            try
            {
                this.width = Float.parseFloat(input);
                this.invalidWidth = false;
            }
            catch (Exception e)
            {
                this.invalidWidth = true;
            }
        });
        this.widthInput.setText(Float.toString(this.container.currentSlide.width));
        this.widthInput.setVisible(true);
        this.children.add(this.widthInput);
        this.heightInput = new TextFieldWidget(this.font, this.guiLeft + 80, this.guiTop + 90, 40, 16, new StringTextComponent("Height"));
        this.heightInput.setResponder(input ->
        {
            try
            {
                this.height = Float.parseFloat(input);
                this.invalidHeight = false;
            }
            catch (Exception e)
            {
                this.invalidHeight = true;
            }
        });
        this.heightInput.setText(Float.toString(this.container.currentSlide.height));
        this.heightInput.setVisible(true);
        this.children.add(this.heightInput);

        this.offsetXInput = new TextFieldWidget(this.font, this.guiLeft + 10, this.guiTop + 130, 40, 16, new StringTextComponent("OffsetX"));
        this.offsetXInput.setResponder(input ->
        {
            try
            {
                this.offsetX = Float.parseFloat(input);
                this.invalidOffsetX = false;
            }
            catch (Exception e)
            {
                this.invalidOffsetX = true;
            }
        });
        this.offsetXInput.setText(Float.toString(this.container.currentSlide.offsetX));
        this.offsetXInput.setVisible(true);
        this.children.add(this.offsetXInput);
        this.offsetYInput = new TextFieldWidget(this.font, this.guiLeft + 60, this.guiTop + 130, 40, 16, new StringTextComponent("OffsetY"));
        this.offsetYInput.setResponder(input ->
        {
            try
            {
                this.offsetY = Float.parseFloat(input);
                this.invalidOffsetY = false;
            }
            catch (Exception e)
            {
                this.invalidOffsetY = true;
            }
        });
        this.offsetYInput.setText(Float.toString(this.container.currentSlide.offsetY));
        this.offsetYInput.setVisible(true);
        this.children.add(this.offsetYInput);
        this.offsetZInput = new TextFieldWidget(this.font, this.guiLeft + 110, this.guiTop + 130, 40, 16, new StringTextComponent("OffsetZ"));
        this.offsetZInput.setResponder(input ->
        {
            try
            {
                this.offsetZ = Float.parseFloat(input);
                this.invalidOffsetZ = false;
            }
            catch (Exception e)
            {
                this.invalidOffsetZ = true;
            }
        });
        this.offsetZInput.setText(Float.toString(this.container.currentSlide.offsetZ));
        this.offsetZInput.setVisible(true);
        this.children.add(this.offsetZInput);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.urlInput.tick();
        this.colorInput.tick();
        this.widthInput.tick();
        this.heightInput.tick();
        this.offsetXInput.tick();
        this.offsetYInput.tick();
        this.offsetZInput.tick();
    }

    @Override
    public void onClose()
    {
        final UpdateImageInfoPacket packet = new UpdateImageInfoPacket();
        packet.pos = this.container.pos;
        packet.data.imageLocation = this.invalidURL ? this.container.currentSlide.imageLocation : this.url;
        packet.data.color = this.invalidColor ? this.container.currentSlide.color : this.color;
        packet.data.width = this.invalidWidth ? this.container.currentSlide.width : this.width;
        packet.data.height = this.invalidHeight ? this.container.currentSlide.height : this.height;
        packet.data.offsetX = this.invalidOffsetX ? this.container.currentSlide.offsetX : this.offsetX;
        packet.data.offsetY = this.invalidOffsetY ? this.container.currentSlide.offsetY : this.offsetY;
        packet.data.offsetZ = this.invalidOffsetZ ? this.container.currentSlide.offsetZ : this.offsetZ;
        SlideShow.channel.sendToServer(packet);
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            this.minecraft.player.closeScreen();
        }

        return this.urlInput.keyPressed(keyCode, scanCode, modifier) || this.urlInput.canWrite()
                || this.colorInput.keyPressed(keyCode, scanCode, modifier) || this.colorInput.canWrite()
                || this.widthInput.keyPressed(keyCode, scanCode, modifier) || this.widthInput.canWrite()
                || this.heightInput.keyPressed(keyCode, scanCode, modifier) || this.heightInput.canWrite()
                || this.offsetXInput.keyPressed(keyCode, scanCode, modifier) || this.offsetXInput.canWrite()
                || this.offsetYInput.keyPressed(keyCode, scanCode, modifier) || this.offsetYInput.canWrite()
                || this.offsetZInput.keyPressed(keyCode, scanCode, modifier) || this.offsetZInput.canWrite()
                || super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.url"), this.guiLeft + 12, this.guiTop + 10, this.invalidURL ? 0xFF5555 : 0x404040);
        this.urlInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.color"), this.guiLeft + 12, this.guiTop + 45, this.invalidColor ? 0xFF5555 : this.color);
        this.colorInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.width"), this.guiLeft + 12, this.guiTop + 80, this.invalidWidth ? 0xFF5555 : 0x404040);
        this.widthInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.height"), this.guiLeft + 82, this.guiTop + 80, this.invalidHeight ? 0xFF5555 : 0x404040);
        this.heightInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.offset_x"), this.guiLeft + 12, this.guiTop + 120, this.invalidOffsetX ? 0xFF5555 : 0x404040);
        this.offsetXInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.offset_y"), this.guiLeft + 62, this.guiTop + 120, this.invalidOffsetY ? 0xFF5555 : 0x404040);
        this.offsetYInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.drawString(matrixStack, I18n.format("gui.slide_show.offset_z"), this.guiLeft + 112, this.guiTop + 120, this.invalidOffsetZ ? 0xFF5555 : 0x404040);
        this.offsetZInput.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderComponentHoverEffect(matrixStack, null, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_BG);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

}