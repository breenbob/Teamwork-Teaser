package conorbreen.com.teamworkteaser.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.ui.UIConstants;
import conorbreen.com.teamworkteaser.ui.fragments.ProjectDetailsDialogFragment;
import conorbreen.com.teamworkteaser.ui.views.ProjectItemLayout;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Conor Breen on 26/01/2018.
 */

public class ProjectListRealmRecyclerViewAdapter extends RealmRecyclerViewAdapter<Project, ProjectListRealmRecyclerViewAdapter.ProjectViewHolder> {
    private Context context;
    private boolean inDeletionMode = false;
    private Set<Integer> countersToDelete = new HashSet<>();

    public ProjectListRealmRecyclerViewAdapter(Context context, RealmResults<Project> data) {
        super(data, true);
        this.context = context;
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true);
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<Integer> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProjectItemLayout itemView = new ProjectItemLayout(context);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        final Project obj = getItem(position);
        holder.data = obj;
        // Dispose of existing subscriptions (in case view is being recycled)
        holder.projectItem.unsubscribe();
        holder.projectItem.setProject(obj);
        holder.setOnClickListener(onClick -> {
            FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
            ProjectDetailsDialogFragment f = ProjectDetailsDialogFragment.newInstance(obj);
            f.show(fm, UIConstants.FragmentTags.ProjectDetails);
        });
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    public interface IClickableViewHolder {
        void onItemSelected(View caller);
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IClickableViewHolder mListener;
        private ProjectItemLayout projectItem;
        public Project data;

        ProjectViewHolder(View view) {
            super(view);
            projectItem = (ProjectItemLayout)view;
            projectItem.getCardView().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemSelected(v);
            }
        }

        public void setOnClickListener(IClickableViewHolder mListener) {
            this.mListener = mListener;
        }
    }
}
