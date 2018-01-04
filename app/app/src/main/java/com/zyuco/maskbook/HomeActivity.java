package com.zyuco.maskbook;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initDrawer();
    }

    private void initDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Log.e("uri", uri.toString());
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.app_name).withIcon(R.mipmap.ic_camera_enhance_black_24dp);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withProfileImagesVisible(true)
                .withHeaderBackground(R.drawable.bg)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz").withIcon("https://wx2.sinaimg.cn/mw690/006fpvB4ly1fn4v9x1cxzj32c0340b2a.jpg")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(1)
                                .withName(R.string.menuitem_home)
                                .withIcon(GoogleMaterial.Icon.gmd_home),
                        new PrimaryDrawerItem()
                                .withIdentifier(2)
                                .withName(R.string.menuitem_lock)
                                .withIcon(GoogleMaterial.Icon.gmd_visibility)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.i("menu", String.valueOf((drawerItem.getIdentifier())));
                        int identifier = (int)drawerItem.getIdentifier();

                        switch (identifier) {
                            case 1:
                                // 主页
                                break;
                            case 2:
                                // 已可见
                                break;
                            case 3:
                                // 注销
                        }
                        return false;
                    }
                })
                .build();

        result.setSelection(1);

        result.addStickyFooterItem(
                new PrimaryDrawerItem()
                        .withIdentifier(3)
                        .withName(R.string.menuitem_logout)
                        .withIcon(GoogleMaterial.Icon.gmd_exit_to_app));
    }
}
