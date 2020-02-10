package com.thebiglosers.phix.view.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Area;
import com.anychart.core.ui.Crosshair;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.graphics.vector.Stroke;
import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.HomeDataModel;
import com.thebiglosers.phix.view.activity.MainActivity;
import com.thebiglosers.phix.view.activity.ProfileActivity;
import com.thebiglosers.phix.viewmodel.HomeDataViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecyclerView;

    @BindView(R.id.layout_data_home)
    LinearLayout layoutHomeData;

    @BindView(R.id.any_chart_view)
    AnyChartView mGraph;

    @BindView(R.id.tv_monthly_expense)
    TextView tvMonthsExpense;

    @BindView(R.id.tv_todays_expense)
    TextView tvTodayExpense;

    @BindView(R.id.error_layout)
    View errorLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private HomeDataViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);

        errorLayout.setVisibility(View.GONE);
        Button myView = (Button) errorLayout.findViewById( R.id.error_layout_retry );
        myView.setOnClickListener(view1 -> onRefresh());

        viewModel = ViewModelProviders.of(this).get(HomeDataViewModel.class);
        viewModel.refresh(((MainActivity) getActivity()).getUniqueUserName());

        shimmerRecyclerView.showShimmerAdapter();
        layoutHomeData.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(this);
        setUserData();
        observeViewModel();
        return view;

    }

    @OnClick(R.id.iv_user_image)
    public void startProfileWindow() {
        startActivity(new Intent(getActivity(), ProfileActivity.class));
    }


    private void observeViewModel() {

        viewModel.mData.observe(getActivity(), dataParameter -> {
            if(dataParameter!=null && dataParameter instanceof HomeDataModel){
                tvMonthsExpense.setText(Float.toString(dataParameter.getMonthsExpense()));
                tvTodayExpense.setText(Float.toString(dataParameter.getTodaysExpense()));
                setUpGraph(dataParameter.getData());
            }
        });

        // for error
        viewModel.imageLoadError.observe(this, isError -> {
            if (isError != null && isError == true){
                shimmerRecyclerView.setVisibility(View.GONE);
                layoutHomeData.setVisibility(View.GONE);
                mGraph.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        // for success
        viewModel.successfullyLoaded.observe(this, loaded -> {
            if (loaded != null && loaded == true){
                shimmerRecyclerView.setVisibility(View.GONE);
                layoutHomeData.setVisibility(View.VISIBLE);
                mGraph.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        // for in progress
        viewModel.loading.observe(getActivity(), isLoading -> {
            if(isLoading!= null  && isLoading instanceof Boolean){
                if(isLoading){
                    shimmerRecyclerView.setVisibility(View.VISIBLE);
                    layoutHomeData.setVisibility(View.GONE);
                    mGraph.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
    }


    private void setUpGraph(Map<Integer, Integer> dataDict) {

        Cartesian areaChart = AnyChart.area();
        areaChart.animation(true);

        Crosshair crosshair = areaChart.crosshair();
        crosshair.enabled(true);
        crosshair.yStroke((Stroke) null, null, null, (String) null, (String) null)
                .xStroke("#6C63FF", 1d, null, (String) null, (String) null)
                .zIndex(39d);
        crosshair.yLabel(0).enabled(true);

        areaChart.yScale().stackMode(ScaleStackMode.VALUE);

        areaChart.title("This months Analysis");

        List<DataEntry> seriesData = new ArrayList<>();

        if (dataDict != null) {
            for (Map.Entry mapElement : dataDict.entrySet()) {
                int key = (int) mapElement.getKey();
                int value = ((int) mapElement.getValue());
                seriesData.add(new CustomDataEntry(key, value));
            }
        }else {
            Log.e("emptyList","GraphError");
            }

            Area series1 = areaChart.area(seriesData);
            series1.stroke("3 #fff");
            series1.hovered().stroke("3 #fff");
            series1.hovered().markers().enabled(true);
            series1.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4d)
                    .stroke("1.5 #fff");
            series1.markers().zIndex(100d);


            areaChart.legend().enabled(true);
            areaChart.legend().fontSize(13d);
            areaChart.legend().padding(0d, 0d, 20d, 0d);

            areaChart.xAxis(0).title(false);
            areaChart.yAxis(0).title("Expenditure");

            areaChart.interactivity().hoverMode(HoverMode.BY_X);
            areaChart.tooltip()
                    .valuePrefix("₹")
                    .displayMode(TooltipDisplayMode.UNION);

            mGraph.setChart(areaChart);
    }

    private void setUserData() {
        Glide.with(getContext())
                .load(((MainActivity) getActivity()).getCurrentUser().getImageString())
                .centerCrop()
                .circleCrop()
                .into(ivUserImage);
        Toast.makeText(getActivity(),((MainActivity) getActivity()).getCurrentUser()
                .getFullName(), Toast.LENGTH_SHORT ).show();
        tvUserName.setText(((MainActivity) getActivity()).getCurrentUser().getFullName());
    }

    @Override
    public void onRefresh() {
        viewModel.refresh(((MainActivity) getActivity()).getUniqueUserName());
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(int x, int value) {
            super(x, value);
        }
    }
}
