package app.mkiniz.poctime.dashboard.adapters;

import app.mkiniz.poctime.dashboard.DashboardQueryUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/dashboard")
@AllArgsConstructor
public class DashboardController {

    private final DashboardQueryUseCase dashboardQueryUseCase;

    @GetMapping(path = "/totals")
    public ResponseEntity<DashboardQueryUseCase.DashboardResponse> getDashboardTotals() {
        return ResponseEntity.ok(dashboardQueryUseCase.execute());
    }
}
