import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SkiStationService } from './services/ski-station.service';
import { ToastService } from './services/toast.service';
import {
  Course,
  Instructor,
  Piste,
  Registration,
  Skier,
  Color,
  Support,
  TypeCourse,
  TypeSubscription
} from './models/ski.models';

type ActiveTab = 'dashboard' | 'pistes' | 'skieurs' | 'moniteurs' | 'cours';

interface DeleteConfirmTarget {
  type: 'piste' | 'skier';
  id: number;
  label: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('viewEnter', [
      transition('* => *', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('240ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('cardStagger', [
      transition('* => *', [
        query(
          ':enter',
          [
            style({ opacity: 0, transform: 'translateY(12px) scale(0.98)' }),
            stagger('32ms', [
              animate('260ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
            ])
          ],
          { optional: true }
        )
      ])
    ]),
    trigger('itemFade', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(8px) scale(0.97)' }),
        animate('240ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
      ]),
      transition(':leave', [
        animate('170ms cubic-bezier(0.4, 0, 1, 1)', style({ opacity: 0, transform: 'scale(0.95)' }))
      ])
    ]),
    trigger('modalBackdrop', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('180ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('140ms ease-in', style({ opacity: 0 }))
      ])
    ]),
    trigger('modalPanel', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(14px) scale(0.96)' }),
        animate('240ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
      ]),
      transition(':leave', [
        animate('140ms ease-in', style({ opacity: 0, transform: 'translateY(6px) scale(0.98)' }))
      ])
    ]),
    trigger('toastAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(28px) scale(0.95)' }),
        animate('260ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateX(0) scale(1)' }))
      ]),
      transition(':leave', [
        animate('170ms ease-in', style({ opacity: 0, transform: 'translateX(24px) scale(0.95)' }))
      ])
    ])
  ]
})
export class AppComponent implements OnInit {
  title = "Station de Ski - Alpe d'Azur";
  activeTab: ActiveTab = 'dashboard';
  mobileMenuOpen = false;

  isBackendConnected = false;
  isLoading = false;
  initialLoading = true;
  backendError = false;
  isSubmitting = false;

  recentlyAddedKey: string | null = null;
  private beaconTimer: ReturnType<typeof setTimeout> | null = null;

  pistes: Piste[] = [];
  skiers: Skier[] = [];
  instructors: Instructor[] = [];
  courses: Course[] = [];

  animatedStats = {
    skiers: 0,
    pistes: 0,
    lengthKm: '0.0',
    instructors: 0,
    revenue: 0
  };
  private countAnimFrame: number | null = null;

  pisteFilterColor = 'ALL';
  pisteSearchTerm = '';
  skierSearchTerm = '';
  skierFilterSubscription = 'ALL';
  courseFilterSupport = 'ALL';

  showAddPisteModal = false;
  showAddSkierModal = false;
  showAddInstructorModal = false;
  showAddCourseModal = false;
  showAssignPisteModal = false;
  showRegisterCourseModal = false;
  deleteConfirmTarget: DeleteConfirmTarget | null = null;

  editingCourseId: number | null = null;

  selectedSkierForPiste: Skier | null = null;
  selectedPisteIdForAssignment: number | null = null;

  selectedSkierForCourse: Skier | null = null;
  selectedCourseIdForReg: number | null = null;
  regNumWeek = 1;

  formErrors: Record<string, string> = {};

  newPiste: Piste = {
    namePiste: '',
    color: 'BLUE',
    length: 1200,
    slope: 18
  };

  newSkier = {
    firstName: '',
    lastName: '',
    dateOfBirth: '2000-05-12',
    city: 'Tunis',
    typeSub: 'ANNUAL' as TypeSubscription,
    startDate: new Date().toISOString().substring(0, 10),
    endDate: this.computeEndDate(new Date().toISOString().substring(0, 10), 'ANNUAL'),
    price: 250.0
  };

  newInstructor = {
    firstName: '',
    lastName: '',
    dateOfHire: new Date().toISOString().substring(0, 10),
    assignCourseId: null as number | null
  };

  newCourse: Course = {
    level: 1,
    typeCourse: 'COLLECTIVE_ADULT',
    support: 'SKI',
    price: 90.0,
    timeSlot: 9
  };

  constructor(
    private skiService: SkiStationService,
    public toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.refreshAllData();
  }

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    if (this.isSubmitting) return;
    if (this.deleteConfirmTarget) {
      this.cancelDelete();
    } else if (this.showAddPisteModal) {
      this.closeAddPisteModal();
    } else if (this.showAddSkierModal) {
      this.closeAddSkierModal();
    } else if (this.showAddInstructorModal) {
      this.closeAddInstructorModal();
    } else if (this.showAddCourseModal) {
      this.closeAddCourseModal();
    } else if (this.showAssignPisteModal) {
      this.closeAssignPisteModal();
    } else if (this.showRegisterCourseModal) {
      this.closeRegisterCourseModal();
    } else if (this.mobileMenuOpen) {
      this.mobileMenuOpen = false;
    }
  }

  setActiveTab(tab: ActiveTab): void {
    this.activeTab = tab;
    this.mobileMenuOpen = false;
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  refreshAllData(): void {
    this.isLoading = true;
    this.backendError = false;

    let pisteSucceeded = false;

    forkJoin({
      pistes: this.skiService.getPistes().pipe(
        catchError(() => {
          return of(null);
        })
      ),
      skiers: this.skiService.getSkiers().pipe(catchError(() => of(null))),
      instructors: this.skiService.getInstructors().pipe(catchError(() => of(null))),
      courses: this.skiService.getCourses().pipe(catchError(() => of(null)))
    }).subscribe((res) => {
      if (res.pistes !== null) {
        this.pistes = res.pistes;
        pisteSucceeded = true;
      }
      if (res.skiers !== null) {
        this.skiers = res.skiers;
      }
      if (res.instructors !== null) {
        this.instructors = res.instructors;
      }
      if (res.courses !== null) {
        this.courses = res.courses;
      }

      this.isBackendConnected = pisteSucceeded || res.skiers !== null;
      this.backendError = !this.isBackendConnected;
      this.isLoading = false;
      this.initialLoading = false;
      this.animateKpiCounters();
    });
  }

  loadPistes(): void {
    this.skiService.getPistes().subscribe({
      next: (data) => {
        this.pistes = data || [];
        this.isBackendConnected = true;
        this.backendError = false;
        this.animateKpiCounters();
      },
      error: () => {
        this.isBackendConnected = false;
      }
    });
  }

  loadSkiers(): void {
    this.skiService.getSkiers().subscribe({
      next: (data) => {
        this.skiers = data || [];
        this.animateKpiCounters();
      }
    });
  }

  loadInstructors(): void {
    this.skiService.getInstructors().subscribe({
      next: (data) => {
        this.instructors = data || [];
        this.animateKpiCounters();
      }
    });
  }

  loadCourses(): void {
    this.skiService.getCourses().subscribe({
      next: (data) => {
        this.courses = data || [];
        this.animateKpiCounters();
      }
    });
  }

  private triggerBeacon(type: string, id?: number): void {
    if (!id) return;
    if (this.beaconTimer) clearTimeout(this.beaconTimer);
    this.recentlyAddedKey = `${type}-${id}`;
    this.beaconTimer = setTimeout(() => {
      this.recentlyAddedKey = null;
    }, 4500);
  }

  private animateKpiCounters(): void {
    if (this.countAnimFrame) {
      cancelAnimationFrame(this.countAnimFrame);
    }

    const startSkiers = this.animatedStats.skiers;
    const startPistes = this.animatedStats.pistes;
    const startInstructors = this.animatedStats.instructors;
    const startRevenue = this.animatedStats.revenue;
    const startLength = parseFloat(this.animatedStats.lengthKm) || 0;

    const targetSkiers = this.totalSkiersCount;
    const targetPistes = this.totalPistesCount;
    const targetInstructors = this.totalInstructorsCount;
    const targetRevenue = this.estimatedTotalRevenue;
    const targetLength = this.pistes.reduce((acc, p) => acc + (p.length || 0), 0) / 1000;

    const duration = 420;
    const startTime = performance.now();

    const tick = (now: number) => {
      const elapsed = Math.min((now - startTime) / duration, 1);
      // Interpolation cubique fluide
      const ease = 1 - Math.pow(1 - elapsed, 3);

      this.animatedStats = {
        skiers: Math.round(startSkiers + (targetSkiers - startSkiers) * ease),
        pistes: Math.round(startPistes + (targetPistes - startPistes) * ease),
        lengthKm: (startLength + (targetLength - startLength) * ease).toFixed(1),
        instructors: Math.round(startInstructors + (targetInstructors - startInstructors) * ease),
        revenue: Math.round(startRevenue + (targetRevenue - startRevenue) * ease)
      };

      if (elapsed < 1) {
        this.countAnimFrame = requestAnimationFrame(tick);
      }
    };

    this.countAnimFrame = requestAnimationFrame(tick);
  }

  openAddPisteModal(): void {
    this.formErrors = {};
    this.newPiste = {
      namePiste: '',
      color: 'BLUE',
      length: 1200,
      slope: 18
    };
    this.showAddPisteModal = true;
  }

  closeAddPisteModal(): void {
    this.showAddPisteModal = false;
    this.formErrors = {};
  }

  savePiste(): void {
    this.formErrors = {};
    if (!this.newPiste.namePiste.trim()) {
      this.formErrors['namePiste'] = 'Le nom de la piste est requis.';
    }
    if (!this.newPiste.length || this.newPiste.length < 50) {
      this.formErrors['length'] = 'Longueur minimale : 50 mètres.';
    }
    if (!this.newPiste.slope || this.newPiste.slope < 1 || this.newPiste.slope > 90) {
      this.formErrors['slope'] = 'La pente doit être comprise entre 1% et 90%.';
    }
    if (Object.keys(this.formErrors).length > 0) return;

    this.isSubmitting = true;
    this.skiService.addPiste({
      ...this.newPiste,
      namePiste: this.newPiste.namePiste.trim()
    }).subscribe({
      next: (saved) => {
        this.isSubmitting = false;
        this.toastService.showSuccess(`Piste "${saved.namePiste}" ajoutée au domaine skiable.`);
        this.closeAddPisteModal();
        this.loadPistes();
        this.triggerBeacon('piste', saved.numPiste);
      },
      error: () => {
        this.isSubmitting = false;
        this.toastService.showError('Impossible de créer la piste. Vérifiez que le backend répond.');
      }
    });
  }

  requestDeletePiste(piste: Piste): void {
    if (!piste.numPiste) return;
    this.deleteConfirmTarget = {
      type: 'piste',
      id: piste.numPiste,
      label: `la piste "${piste.namePiste}"`
    };
  }

  openAddSkierModal(): void {
    this.formErrors = {};
    const today = new Date().toISOString().substring(0, 10);
    this.newSkier = {
      firstName: '',
      lastName: '',
      dateOfBirth: '2000-05-12',
      city: 'Tunis',
      typeSub: 'ANNUAL',
      startDate: today,
      endDate: this.computeEndDate(today, 'ANNUAL'),
      price: 250.0
    };
    this.showAddSkierModal = true;
  }

  closeAddSkierModal(): void {
    this.showAddSkierModal = false;
    this.formErrors = {};
  }

  onSubscriptionTypeChange(): void {
    this.newSkier.endDate = this.computeEndDate(this.newSkier.startDate, this.newSkier.typeSub);
    if (this.newSkier.typeSub === 'ANNUAL') {
      this.newSkier.price = 250.0;
    } else if (this.newSkier.typeSub === 'SEMESTRIEL') {
      this.newSkier.price = 140.0;
    } else {
      this.newSkier.price = 45.0;
    }
  }

  computeEndDate(startDateStr: string, type: TypeSubscription): string {
    const d = new Date(startDateStr);
    if (isNaN(d.getTime())) return startDateStr;
    if (type === 'ANNUAL') {
      d.setFullYear(d.getFullYear() + 1);
    } else if (type === 'SEMESTRIEL') {
      d.setMonth(d.getMonth() + 6);
    } else {
      d.setMonth(d.getMonth() + 1);
    }
    return d.toISOString().substring(0, 10);
  }

  saveSkier(): void {
    this.formErrors = {};
    if (!this.newSkier.firstName.trim()) {
      this.formErrors['firstName'] = 'Le prénom est obligatoire.';
    }
    if (!this.newSkier.lastName.trim()) {
      this.formErrors['lastName'] = 'Le nom est obligatoire.';
    }
    if (!this.newSkier.city.trim()) {
      this.formErrors['city'] = 'Veuillez indiquer la ville.';
    }
    if (!this.newSkier.dateOfBirth) {
      this.formErrors['dateOfBirth'] = 'Date de naissance requise.';
    }
    if (Object.keys(this.formErrors).length > 0) return;

    const payload: Skier = {
      firstName: this.newSkier.firstName.trim(),
      lastName: this.newSkier.lastName.trim(),
      dateOfBirth: this.newSkier.dateOfBirth,
      city: this.newSkier.city.trim(),
      subscription: {
        startDate: this.newSkier.startDate,
        endDate: this.newSkier.endDate,
        price: this.newSkier.price,
        typeSub: this.newSkier.typeSub
      }
    };

    this.isSubmitting = true;
    this.skiService.addSkier(payload).subscribe({
      next: (saved) => {
        this.isSubmitting = false;
        this.toastService.showSuccess(`Skieur ${saved.firstName} ${saved.lastName} inscrit avec forfait ${this.getSubscriptionLabel(saved.subscription?.typeSub)}.`);
        this.closeAddSkierModal();
        this.loadSkiers();
        this.triggerBeacon('skier', saved.numSkier);
      },
      error: () => {
        this.isSubmitting = false;
        this.toastService.showError('Erreur lors de l\'enregistrement du skieur.');
      }
    });
  }

  requestDeleteSkier(skier: Skier): void {
    if (!skier.numSkier) return;
    this.deleteConfirmTarget = {
      type: 'skier',
      id: skier.numSkier,
      label: `le skieur ${skier.firstName} ${skier.lastName}`
    };
  }

  cancelDelete(): void {
    this.deleteConfirmTarget = null;
  }

  confirmDelete(): void {
    if (!this.deleteConfirmTarget) return;
    const { type, id, label } = this.deleteConfirmTarget;
    this.isSubmitting = true;

    if (type === 'piste') {
      this.skiService.deletePiste(id).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.pistes = this.pistes.filter(p => p.numPiste !== id);
          this.deleteConfirmTarget = null;
          this.toastService.showSuccess(`Suppression confirmée pour ${label}.`);
          this.animateKpiCounters();
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError(`Impossible de supprimer ${label}.`);
        }
      });
    } else if (type === 'skier') {
      this.skiService.deleteSkier(id).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.skiers = this.skiers.filter(s => s.numSkier !== id);
          this.deleteConfirmTarget = null;
          this.toastService.showSuccess(`Suppression confirmée pour ${label}.`);
          this.animateKpiCounters();
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError(`Impossible de supprimer ${label}.`);
        }
      });
    }
  }

  openAssignPisteModal(skier: Skier): void {
    this.selectedSkierForPiste = skier;
    this.selectedPisteIdForAssignment = this.pistes.length > 0 ? (this.pistes[0].numPiste || null) : null;
    this.showAssignPisteModal = true;
  }

  closeAssignPisteModal(): void {
    this.showAssignPisteModal = false;
    this.selectedSkierForPiste = null;
    this.selectedPisteIdForAssignment = null;
  }

  submitAssignPiste(): void {
    if (!this.selectedSkierForPiste?.numSkier || !this.selectedPisteIdForAssignment) {
      this.toastService.showWarning('Veuillez sélectionner une piste disponible.');
      return;
    }
    this.isSubmitting = true;
    const targetName = this.selectedSkierForPiste.firstName;
    const targetId = this.selectedSkierForPiste.numSkier;

    this.skiService.assignSkierToPiste(targetId, this.selectedPisteIdForAssignment).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.toastService.showSuccess(`Piste affectée à ${targetName} avec succès.`);
        this.closeAssignPisteModal();
        this.loadSkiers();
        this.loadPistes();
        this.triggerBeacon('skier', targetId);
      },
      error: () => {
        this.isSubmitting = false;
        this.toastService.showError('Erreur lors de l\'affectation de la piste.');
      }
    });
  }

  openRegisterCourseModal(skier: Skier): void {
    this.selectedSkierForCourse = skier;
    this.selectedCourseIdForReg = this.courses.length > 0 ? (this.courses[0].numCourse || null) : null;
    this.regNumWeek = 1;
    this.showRegisterCourseModal = true;
  }

  closeRegisterCourseModal(): void {
    this.showRegisterCourseModal = false;
    this.selectedSkierForCourse = null;
    this.selectedCourseIdForReg = null;
  }

  submitRegisterCourse(): void {
    if (!this.selectedSkierForCourse?.numSkier || !this.selectedCourseIdForReg) {
      this.toastService.showWarning('Veuillez choisir un cours pour l\'inscription.');
      return;
    }

    const registration: Registration = {
      numWeek: this.regNumWeek
    };

    const skierId = this.selectedSkierForCourse.numSkier;
    const skierName = this.selectedSkierForCourse.firstName;

    this.isSubmitting = true;
    this.skiService.addRegistrationAndAssignToSkierAndCourse(
      registration,
      skierId,
      this.selectedCourseIdForReg
    ).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (res && res.numRegistration) {
          this.toastService.showSuccess(`${skierName} inscrit au cours (Semaine ${this.regNumWeek}).`);
          this.closeRegisterCourseModal();
          this.loadSkiers();
          this.triggerBeacon('skier', skierId);
        } else {
          this.toastService.showWarning(
            'Inscription refusée : vérifiez l\'âge du skieur (< 16 ans Enfants / ≥ 16 ans Adultes), les doublons ou la limite de 6 places.'
          );
        }
      },
      error: () => {
        this.isSubmitting = false;
        this.toastService.showError('Échec de l\'inscription au cours.');
      }
    });
  }

  openAddInstructorModal(): void {
    this.formErrors = {};
    this.newInstructor = {
      firstName: '',
      lastName: '',
      dateOfHire: new Date().toISOString().substring(0, 10),
      assignCourseId: null
    };
    this.showAddInstructorModal = true;
  }

  closeAddInstructorModal(): void {
    this.showAddInstructorModal = false;
    this.formErrors = {};
  }

  saveInstructor(): void {
    this.formErrors = {};
    if (!this.newInstructor.firstName.trim()) {
      this.formErrors['instFirstName'] = 'Le prénom du moniteur est requis.';
    }
    if (!this.newInstructor.lastName.trim()) {
      this.formErrors['instLastName'] = 'Le nom du moniteur est requis.';
    }
    if (Object.keys(this.formErrors).length > 0) return;

    const payload: Instructor = {
      firstName: this.newInstructor.firstName.trim(),
      lastName: this.newInstructor.lastName.trim(),
      dateOfHire: this.newInstructor.dateOfHire
    };

    this.isSubmitting = true;
    if (this.newInstructor.assignCourseId) {
      this.skiService.addInstructorAndAssignToCourse(payload, this.newInstructor.assignCourseId).subscribe({
        next: (saved) => {
          this.isSubmitting = false;
          this.toastService.showSuccess(`Moniteur ${saved.firstName} ${saved.lastName} ajouté et assigné.`);
          this.closeAddInstructorModal();
          this.loadInstructors();
          this.triggerBeacon('instructor', saved.numInstructor);
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError('Erreur lors de l\'ajout du moniteur.');
        }
      });
    } else {
      this.skiService.addInstructor(payload).subscribe({
        next: (saved) => {
          this.isSubmitting = false;
          this.toastService.showSuccess(`Moniteur ${saved.firstName} ${saved.lastName} enregistré.`);
          this.closeAddInstructorModal();
          this.loadInstructors();
          this.triggerBeacon('instructor', saved.numInstructor);
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError('Erreur lors de l\'ajout du moniteur.');
        }
      });
    }
  }

  openAddCourseModal(): void {
    this.editingCourseId = null;
    this.formErrors = {};
    this.newCourse = {
      level: 1,
      typeCourse: 'COLLECTIVE_ADULT',
      support: 'SKI',
      price: 90.0,
      timeSlot: 9
    };
    this.showAddCourseModal = true;
  }

  openEditCourseModal(course: Course): void {
    this.editingCourseId = course.numCourse || null;
    this.formErrors = {};
    this.newCourse = {
      numCourse: course.numCourse,
      level: course.level,
      typeCourse: course.typeCourse,
      support: course.support,
      price: course.price,
      timeSlot: course.timeSlot
    };
    this.showAddCourseModal = true;
  }

  closeAddCourseModal(): void {
    this.showAddCourseModal = false;
    this.editingCourseId = null;
    this.formErrors = {};
  }

  saveCourse(): void {
    this.formErrors = {};
    if (this.newCourse.level < 1 || this.newCourse.level > 5) {
      this.formErrors['courseLevel'] = 'Niveau compris entre 1 et 5.';
    }
    if (this.newCourse.price < 10) {
      this.formErrors['coursePrice'] = 'Tarif minimal : 10 DT.';
    }
    if (Object.keys(this.formErrors).length > 0) return;

    this.isSubmitting = true;
    if (this.editingCourseId) {
      this.skiService.updateCourse(this.newCourse).subscribe({
        next: (updated) => {
          this.isSubmitting = false;
          this.toastService.showSuccess(`Cours #${updated.numCourse} mis à jour.`);
          this.closeAddCourseModal();
          this.loadCourses();
          this.triggerBeacon('course', updated.numCourse);
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError('Erreur lors de la mise à jour du cours.');
        }
      });
    } else {
      this.skiService.addCourse(this.newCourse).subscribe({
        next: (saved) => {
          this.isSubmitting = false;
          this.toastService.showSuccess(`Nouveau cours #${saved.numCourse} (${saved.support}) créé.`);
          this.closeAddCourseModal();
          this.loadCourses();
          this.triggerBeacon('course', saved.numCourse);
        },
        error: () => {
          this.isSubmitting = false;
          this.toastService.showError('Erreur lors de la création du cours.');
        }
      });
    }
  }

  // Projection g�om�trique de la pente en coordonn�es SVG (viewBox 0 0 96 44)
  getSlopeSvgLine(slopePercent: number): string {
    const clamped = Math.max(5, Math.min(slopePercent || 15, 65));
    const drop = Math.round(10 + (clamped / 65) * 26);
    const startY = 40 - drop;
    return `M 4 ${startY} L 92 38`;
  }

  getSlopePolygon(slopePercent: number): string {
    const clamped = Math.max(5, Math.min(slopePercent || 15, 65));
    const drop = Math.round(10 + (clamped / 65) * 26);
    const startY = 40 - drop;
    return `4,38 4,${startY} 92,38`;
  }

  get totalSkiersCount(): number {
    return this.skiers.length;
  }

  get totalPistesCount(): number {
    return this.pistes.length;
  }

  get totalSlopeLengthKm(): string {
    const totalMeters = this.pistes.reduce((acc, p) => acc + (p.length || 0), 0);
    return (totalMeters / 1000).toFixed(1);
  }

  get totalInstructorsCount(): number {
    return this.instructors.length;
  }

  get estimatedTotalRevenue(): number {
    return this.skiers.reduce((acc, s) => acc + (s.subscription?.price || 0), 0);
  }

  getPistesByColorCount(color: Color): number {
    return this.pistes.filter(p => p.color === color).length;
  }

  get filteredPistes(): Piste[] {
    return this.pistes.filter(p => {
      const matchColor = this.pisteFilterColor === 'ALL' || p.color === this.pisteFilterColor;
      const matchSearch = !this.pisteSearchTerm || p.namePiste.toLowerCase().includes(this.pisteSearchTerm.toLowerCase());
      return matchColor && matchSearch;
    });
  }

  get filteredSkiers(): Skier[] {
    return this.skiers.filter(s => {
      const matchSub = this.skierFilterSubscription === 'ALL' || s.subscription?.typeSub === this.skierFilterSubscription;
      const term = this.skierSearchTerm.toLowerCase();
      const matchSearch = !this.skierSearchTerm ||
        s.firstName.toLowerCase().includes(term) ||
        s.lastName.toLowerCase().includes(term) ||
        (s.city && s.city.toLowerCase().includes(term));
      return matchSub && matchSearch;
    });
  }

  get filteredCourses(): Course[] {
    return this.courses.filter(c => {
      return this.courseFilterSupport === 'ALL' || c.support === this.courseFilterSupport;
    });
  }

  getColorLabel(color: Color): string {
    switch (color) {
      case 'GREEN': return 'Verte · Débutant';
      case 'BLUE': return 'Bleue · Intermédiaire';
      case 'RED': return 'Rouge · Avancé';
      case 'BLACK': return 'Noire · Expert';
      default: return color;
    }
  }

  getTypeCourseLabel(type: TypeCourse): string {
    switch (type) {
      case 'COLLECTIVE_CHILDREN': return 'Collectif Enfants';
      case 'COLLECTIVE_ADULT': return 'Collectif Adultes';
      case 'INDIVIDUAL': return 'Cours Individuel';
      default: return type;
    }
  }

  getSubscriptionLabel(type?: TypeSubscription): string {
    if (!type) return 'Sans forfait';
    switch (type) {
      case 'ANNUAL': return 'Annuel';
      case 'SEMESTRIEL': return 'Semestriel';
      case 'MONTHLY': return 'Mensuel';
      default: return type;
    }
  }
}
