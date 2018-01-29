package conorbreen.com.teamworkteaser.ui.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.services.TeamworkApiService;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.utils.ApiUtils;
import conorbreen.com.teamworkteaser.utils.UnitUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Conor Breen on 28/01/2018.
 */

public class ProjectItemLayout extends LinearLayout {
    //region View bindings
    @BindView(R.id.cvProjectItem)
    CardView cvProjectItem;

    @BindView(R.id.ivProjectLogo)
    ImageView ivProjectLogo;

    @BindView(R.id.cbStarred)
    CheckBox cbStarred;

    @BindView(R.id.tvProjectName)
    TextView tvProjectName;

    @BindView(R.id.tvProjectDesc)
    TextView tvProjectDesc;
    //endregion

    private Disposable disposable;

    public ProjectItemLayout(Context context) {
        super(context);
        init();
    }

    public ProjectItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProjectItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_project_item, this);
        ButterKnife.bind(this);

        // The card view elevation/shadows requires padding in parent view to display correctly...
        // ...but any padding added declaratively in XML layout root element is lost when view is inflated as attributes are merged
        int paddingTopBottom = (int) getResources().getDimension(R.dimen.cardview_top_bottom_margin);
        int paddingLeftRight = (int) getResources().getDimension(R.dimen.cardview_left_right_margin);

        // we use 0 for top to help keep padding equal for all items in out RecyclerView
        // RecyclerView itself has top padding, then each item only has bottom padding
        // Otherwise the padding in between items would be twice that of the top padding of the first item
        // or the bottom padding of the last item!
        setPadding(paddingLeftRight, 0, paddingLeftRight, paddingTopBottom);
        setClipToPadding(false);
    }

    public void setProject(Project project)
    {
        tvProjectName.setText(project.getName());
        tvProjectDesc.setText(project.getDescription());
        cbStarred.setChecked(project.isStarred());

        // Use Jake Wharton's RxBindings plugin to call Teamwork Api and update Realmobject on checked change
        disposable = RxCompoundButton
                .checkedChanges(cbStarred)
                .skipInitialValue()
                .debounce(0, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(checked -> setRealmObjectStarred(project, checked));

        // Important to use this when using Glide in a RecyclerView, to clear any pending requests.
        Glide.with(getContext())
                .clear(ivProjectLogo);

        if (!TextUtils.isEmpty(project.getLogo())) {
            ColorDrawable placeholderGrey = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorLightGrey));

            Glide.with(getContext())
                    .load(project.getLogo())
                    .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(placeholderGrey))
                    .into(ivProjectLogo);
        } else {
            Drawable noImageDrawable;

            if (ApiUtils.LollipopOrNewer()) {
                noImageDrawable = getResources().getDrawable(R.drawable.ic_placeholder_black_24dp, null);
            } else {
                noImageDrawable = getResources().getDrawable(R.drawable.ic_placeholder_black_24dp);
            }

            int padding = UnitUtils.dpToPx(20);

            noImageDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGrey), PorterDuff.Mode.SRC_IN);
            ivProjectLogo.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLightGrey));
            ivProjectLogo.setImageDrawable(noImageDrawable);
            ivProjectLogo.setPadding(padding, padding, padding, padding);
        }
    }

    private void setRealmObjectStarred(Project project, boolean starred) {
        TeamworkRealmService.getInstance().updatedManagedObject(transaction -> project.setStarred(starred),
                onSuccess ->{
                    if (starred) {
                        starRemoteProject(project);
                    } else {
                        unstarRemoteProject(project);
                    }
                },
                () -> Snackbar.make(cbStarred, starred ? R.string.snackbar_star_error : R.string.snackbar_unstar_error, Snackbar.LENGTH_SHORT).show());
    }

    private void starRemoteProject(Project project) {
        TeamworkApiService
                .getInstance()
                .starProject(project.getId())
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project starred: %s" : "Project could not be starred: %s", response.message());
                    if (!response.isSuccessful()) {
                        // Unstar the project again as remote 'starring' failed.
                        setRealmObjectStarred(project, false);
                        Snackbar.make(cbStarred, R.string.snackbar_star_error, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(cbStarred, R.string.snackbar_star_success, Snackbar.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Timber.e(throwable, "StarProject Api call failed with error");

                    // Unstar the project again as remote 'starring' failed.
                    setRealmObjectStarred(project, false);
                    Snackbar.make(cbStarred, R.string.snackbar_star_error, Snackbar.LENGTH_SHORT).show();
                });
    }

    private void unstarRemoteProject(Project project) {
        TeamworkApiService
                .getInstance()
                .unstarProject(project.getId())
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project unstarred: %s" : "Project could not be unstarred: %s", response.message());
                    if (!response.isSuccessful()) {
                        // Star the project again as remote 'unstarring' failed.
                        setRealmObjectStarred(project, true);
                        Snackbar.make(cbStarred, R.string.snackbar_unstar_error, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(cbStarred, R.string.snackbar_unstar_success, Snackbar.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Timber.e(throwable, "UnstarProject Api call failed with error");

                    // Star the project again as remote 'unstarring' failed.
                    setRealmObjectStarred(project, true);
                    Snackbar.make(cbStarred, R.string.snackbar_unstar_error, Snackbar.LENGTH_SHORT).show();
                });
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
