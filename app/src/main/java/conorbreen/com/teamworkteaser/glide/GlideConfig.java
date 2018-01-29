package conorbreen.com.teamworkteaser.glide;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Conor Breen on 28/01/2018.
 */

@GlideModule
public class GlideConfig extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Up the image quality default - uses more memory, but default value yields unacceptably poor image quality
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888));
    }
}
