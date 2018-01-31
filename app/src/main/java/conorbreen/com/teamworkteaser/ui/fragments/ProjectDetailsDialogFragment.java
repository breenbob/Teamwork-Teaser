package conorbreen.com.teamworkteaser.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.BuildConfig;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.Category;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.services.TeamworkApiService;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.ui.UIConstants;
import conorbreen.com.teamworkteaser.ui.adapters.CategoryArrayAdapter;
import io.reactivex.disposables.CompositeDisposable;

import timber.log.Timber;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class ProjectDetailsDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, Validator.ValidationListener {
    //region View bindings
    @NotEmpty(messageResId = R.string.val_project_name_req)
    @BindView(R.id.tvProjectName)
    TextView tvProjectName;
    @BindView(R.id.tvProjectDesc)
    TextView tvProjectDesc;
    @BindView(R.id.etProjectName)
    EditText etProjectName;
    @BindView(R.id.etProjectDesc)
    EditText etProjectDesc;
    @BindView(R.id.tvProjectStartDate)
    TextView tvProjectStartDate;
    @BindView(R.id.btnProjectStartDate)
    Button btnProjectStartDate;
    @BindView(R.id.tvProjectEndDate)
    TextView tvProjectEndDate;
    @BindView(R.id.btnProjectEndDate)
    Button btnProjectEndDate;
    @BindView(R.id.tvProjectCategory)
    TextView tvProjectCategory;
    // Category is not actually required
    //@Select
    @BindView(R.id.spnCategory)
    Spinner spnCategory;

    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnSaveProject)
    Button btnSaveProject;
    //endregion

    private TeamworkRealmService realmService = TeamworkRealmService.getInstance();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Project project;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

    // Store these in temp variables so as not to update the RealmObject until we are saving.
    private Date startDate = null;
    private Date endDate = null;
    private Validator validator;

    public static ProjectDetailsDialogFragment newInstance(Project project) {
        ProjectDetailsDialogFragment f = new ProjectDetailsDialogFragment();

        f.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        Bundle args = new Bundle();
        args.putParcelable(UIConstants.BundleKeys.ProjectParcelKey, project);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_project_add_edit, container);
        ButterKnife.bind(this, v);

        if (getArguments() != null && getArguments().containsKey(UIConstants.BundleKeys.ProjectParcelKey)) {
            project = getArguments().getParcelable(UIConstants.BundleKeys.ProjectParcelKey);
            setInitialValues();
            getDialog().setTitle(getResources().getString(R.string.dialog_edit_project));
        } else {
            getDialog().setTitle(getResources().getString(R.string.dialog_create_project));
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        disposables.add(RxView.clicks(btnProjectStartDate).subscribe(onClick -> onSetStartDateClick()));
        disposables.add(RxView.clicks(btnProjectEndDate).subscribe(onClick -> onSetEndDateClick()));
        disposables.add(RxView.clicks(btnCancel).subscribe(onClick -> dismiss()));
        // Trigger async validation, which will in turn call onValidationSucceeded/Failed callbacks below to save object/show errors.
        disposables.add(RxView.clicks(btnSaveProject).subscribe(onClick -> validator.validate()));
        disposables.add(realmService
                .getAllCategories()
                .subscribe(
                        categories -> {
                            Timber.d("RealmService results loaded: %s", categories.isLoaded());
                            setupSpinner(realmService.copyFromRealm(categories));
                        },
                        throwable -> Timber.e(throwable, "An error occurred when fetching the category list:")
                ));

        return v;
    }

    private void setInitialValues() {
        etProjectName.setText(project.getName());
        etProjectDesc.setText(project.getDescription());

        if (project.getStartDate() != null) {
            startDate = project.getStartDate();
            btnProjectStartDate.setText(formatter.format(startDate));
        }

        if (project.getEndDate() != null) {
            endDate = project.getEndDate();
            btnProjectEndDate.setText(formatter.format(endDate));
        }
    }

    private void setupSpinner(List<Category> categories) {
        CategoryArrayAdapter arrayAdapter = new CategoryArrayAdapter(getContext(), categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(arrayAdapter);
    }

    private void onSetStartDateClick() {

        DatePickerDialog dpd;

        if (btnProjectStartDate.getText().toString().equals(getResources().getString(R.string.hint_select_date))) {
            Calendar now = Calendar.getInstance();
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            Date selectedDate;
            try {
                selectedDate = formatter.parse(btnProjectStartDate.getText().toString());
            } catch (ParseException ex) {
                Timber.d(ex, "Error parsing selected start date '%s' in format 'dd/MM/yyyy'", btnProjectStartDate.getText());
                selectedDate = new Date();
            }

            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            dpd = DatePickerDialog.newInstance(
                    this,
                    selectedCal.get(Calendar.YEAR),
                    selectedCal.get(Calendar.MONTH),
                    selectedCal.get(Calendar.DAY_OF_MONTH)
            );
        }

        if (getActivity() != null) {
            dpd.show(getActivity().getFragmentManager(), "StartDatePicker");
        }
    }

    private void onSetEndDateClick() {

        DatePickerDialog dpd;

        if (btnProjectEndDate.getText().toString().equals(getResources().getString(R.string.hint_select_date))) {
            Calendar now = Calendar.getInstance();
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            Date selectedDate;
            try {
                selectedDate = formatter.parse(btnProjectEndDate.getText().toString());
            } catch (ParseException ex) {
                Timber.d(ex, "Error parsing selected end date '%s' in format 'dd/MM/yyyy'", btnProjectEndDate.getText());
                selectedDate = new Date();
            }

            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            dpd = DatePickerDialog.newInstance(
                    this,
                    selectedCal.get(Calendar.YEAR),
                    selectedCal.get(Calendar.MONTH),
                    selectedCal.get(Calendar.DAY_OF_MONTH)
            );
        }

        if (getActivity() != null) {
            dpd.show(getActivity().getFragmentManager(), "EndDatePicker");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        switch (view.getTag())
        {
            case "StartDatePicker":
            {
                try {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    startDate = calendar.getTime();
                    btnProjectStartDate.setText(formatter.format(startDate));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case "EndDatePicker":
            {
                try {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    endDate = calendar.getTime();
                    btnProjectEndDate.setText(formatter.format(endDate));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        saveProject();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else if (view instanceof Spinner) {
                ((TextView) ((Spinner) view).getSelectedView()).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveProject() {
        // if new project, create new RealmObject and set default values
        if (project == null) {
            project = new Project();
            project.setCompanyId(BuildConfig.TeamworkCompanyId);
            project.setHarvestTimersEnabled(true);
            project.setReplyByEmailEnabled(true);
            project.setPrivacyEnabled(true);

            updateFromInputFields();
            createRemoteProject();
        } else {
            // Need to copy out of Realm so we can update fields outside of a transaction -
            // We need to update fields so we can post model to API, and we want to do that
            // before updating the local model (as no easy way to handle local primary key vs remote primary key otherwise)
            project = realmService.copyFromRealm(project);

            updateFromInputFields();
            updateRemoteProject();
        }
    }

    private void updateFromInputFields() {
        project.setName(etProjectName.getText().toString());
        project.setDescription(etProjectDesc.getText().toString());

        // If startDate is null, that's ok - it's an optional field anyway by the looks of it
        project.setStartDate(startDate);

        // same for endDate
        project.setEndDate(endDate);

        if (spnCategory.getSelectedItem() != null) {
            Category category = (Category)spnCategory.getSelectedItem();
            if (category != null) {
                project.setCategoryId(category.getId());
            }
        }
    }

    private void createRemoteProject() {
        TeamworkApiService
                .getInstance()
                .createProject(project)
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project created: %s" : "Project could not be created: %s", response.message());

                    if (!response.isSuccessful()) {
                        String errorMsg = String.format(getResources().getString(R.string.snackbar_create_project_failed), response.message());
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        // Since the API does not return new project id, it is safer to sync local data with API again rather than
                        // assume any kind of local primary key
                        Toast.makeText(getContext(), R.string.snackbar_create_project_succeeded, Toast.LENGTH_LONG).show();
                        getTargetFragment().onActivityResult(getTargetRequestCode(), UIConstants.ResponseCodes.CreateSucceeded, null);
                        dismiss();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Project create Api call failed with error");
                    Toast.makeText(getContext(), R.string.snackbar_create_project_failed, Toast.LENGTH_LONG).show();
                });
    }

    private void updateRemoteProject() {
        TeamworkApiService
                .getInstance()
                .updateProject(project)
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project updated: %s" : "Project could not be updated: %s", response.message());

                    if (!response.isSuccessful()) {
                        String errorMsg = String.format(getResources().getString(R.string.snackbar_update_project_failed), response.message());
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        // For project updates it is ok to update the local Realm object
                        realmService.updatedManagedObject(realm -> realm.copyToRealmOrUpdate(project),
                                () -> {
                                    Toast.makeText(getContext(), R.string.snackbar_update_project_succeeded, Toast.LENGTH_LONG).show();
                                    dismiss();
                                },
                                throwable -> {
                                    Timber.e(throwable, "Error updating local project in realm");
                                    String errorMsg = String.format(getResources().getString(R.string.snackbar_update_project_failed), "Error saving to local database.");
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                                });
                    }
                }, throwable -> {
                    Timber.e(throwable, "Project update Api call failed with error");
                    Toast.makeText(getContext(), R.string.snackbar_update_project_failed, Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
