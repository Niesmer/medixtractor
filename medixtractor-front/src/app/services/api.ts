// API service to handle all backend communication

const API_BASE = '/api';

// Helper to detect and provide better error messages
function parseApiError(text: string, endpoint: string, status: number): string {
  // Check if response is HTML (error page)
  if (text.includes('<!doctype') || text.includes('<html')) {
    return `API Error (${status}): ${endpoint} - Server returned HTML instead of JSON. Check that the backend server is running at the correct URL.`;
  }

  // Check if response is plain text error
  if (text && !text.startsWith('{') && !text.startsWith('[')) {
    return `API Error (${status}): ${endpoint} - ${text.substring(0, 200)}`;
  }

  return `API Error (${status}): ${endpoint}`;
}

export interface Filters {
  substances: string[];
  formes: string[];
  statuts: string[];
  laboratoires: string[];
}

export interface DatabaseStatus {
  medicaments: number;
  presentations: number;
  compositions: number;
}

export interface ImportStatus {
  message: string;
  succes: boolean;
  sourceDir?: string;
  tentativeEffectuee?: boolean;
  fichierMedicaments?: ImportReport;
  fichierPresentations?: ImportReport;
  fichierCompositions?: ImportReport;
  medicamentsEnBase?: number;
  presentationsEnBase?: number;
  compositionsEnBase?: number;
}

export interface ImportReport {
  fichier: string;
  lignesImportees: number;
  lignesIgnorees: number;
  lignesInvalides: number;
  lignesLues: number;
}

export interface Medicament {
  cis: string;
  name: string;
  status: string;
  pharmaceuticalForm: string;
  administrationRoute: string;
  laboratory: string;
  activeSubstances: string[];
}

export interface MedicamentDetail extends Medicament {
  procedure: string;
  commercialization: string;
  marketingDate: string;
  compositions: Array<{
    substance: string;
    dosage: string;
  }>;
  presentations: Array<{
    cip: string;
    prix: string;
    remboursement: string;
  }>;
}

export interface SignUpParams {
  fullName: string;
  email: string;
  password: string;
  role: "DOCTOR" | "PHARMACIST" | "ADMIN";
  siretSiren?: string;
}

export interface LoginParams {
  email: string;
  password: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  token: string;
  user: {
    id?: string;
    email: string;
    fullName: string;
    role: string;
  };
}

export interface FilterParams {
  query?: string;
  substance?: string;
  forme?: string;
  statut?: string;
  laboratoire?: string;
}

// Get available filters
export async function getFilters(): Promise<Filters> {
  try {
    const response = await fetch(`${API_BASE}/filters`);
    const text = await response.text();

    console.log('Filters response status:', response.status);
    console.log('Filters response text:', text.substring(0, 200));

    if (!response.ok) {
      throw new Error(text || `HTTP ${response.status}: Failed to fetch filters`);
    }

    if (!text) {
      return { substances: [], formes: [], statuts: [], laboratoires: [] };
    }

    try {
      return JSON.parse(text);
    } catch (parseError) {
      console.error('JSON parse error in filters. Response was:', text);
      return { substances: [], formes: [], statuts: [], laboratoires: [] };
    }
  } catch (e) {
    console.error('Filters error:', e);
    return { substances: [], formes: [], statuts: [], laboratoires: [] };
  }
}

// Get compatible filters based on current selection
export async function getCompatibleFilters(params: FilterParams): Promise<Filters> {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value) {
      searchParams.set(key, value);
    }
  });

  try {
    const response = await fetch(`${API_BASE}/filters/compatibles?${searchParams}`);
    const text = await response.text();

    console.log('Compatible filters response status:', response.status);
    console.log('Compatible filters response text:', text.substring(0, 200));

    if (!response.ok) {
      console.error('Failed to fetch compatible filters:', text);
      throw new Error('Failed to fetch compatible filters');
    }

    if (!text) {
      return { substances: [], formes: [], statuts: [], laboratoires: [] };
    }

    try {
      return JSON.parse(text);
    } catch (parseError) {
      console.error('JSON parse error in compatible filters. Response was:', text);
      throw new Error('Failed to fetch compatible filters');
    }
  } catch (e) {
    console.error('Compatible filters error:', e);
    throw e;
  }
}

// Get database status
export async function getDatabaseStatus(): Promise<DatabaseStatus> {
  try {
    const response = await fetch(`${API_BASE}/imports/statut`);
    const text = await response.text();

    console.log('Database status response:', response.status);
    console.log('Database status text:', text.substring(0, 200));

    if (!response.ok) {
      throw new Error(text || `HTTP ${response.status}: Failed to fetch database status`);
    }

    if (!text) {
      return { medicaments: 0, presentations: 0, compositions: 0 };
    }

    try {
      return JSON.parse(text);
    } catch (parseError) {
      console.error('JSON parse error in database status. Response was:', text);
      return { medicaments: 0, presentations: 0, compositions: 0 };
    }
  } catch (e) {
    console.error('Database status error:', e);
    return { medicaments: 0, presentations: 0, compositions: 0 };
  }
}

// Get startup import status
export async function getStartupImportStatus(): Promise<ImportStatus> {
  try {
    const response = await fetch(`${API_BASE}/imports/demarrage`);
    const text = await response.text();

    console.log('Startup import status response:', response.status);
    console.log('Startup import status text:', text.substring(0, 200));

    if (!response.ok) {
      return {
        message: `Etat de l'import automatique indisponible (HTTP ${response.status})${text ? ` : ${text}` : '.'}`,
        succes: false,
        tentativeEffectuee: true
      };
    }

    if (!text) {
      return {
        message: 'Aucune donnée d\'import',
        succes: false
      };
    }

    try {
      return JSON.parse(text);
    } catch (parseError) {
      console.error('JSON parse error in startup import status. Response was:', text);
      return {
        message: 'Erreur lors de la vérification de l\'import',
        succes: false
      };
    }
  } catch (e) {
    console.error('Startup import status error:', e);
    return {
      message: 'Erreur lors de la vérification de l\'import',
      succes: false
    };
  }
}

// Search medicaments
export async function searchMedicaments(params: FilterParams): Promise<Medicament[]> {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value) {
      searchParams.set(key, value);
    }
  });

  const endpoint = `/api/medicaments?${searchParams}`;

  try {
    const response = await fetch(`${API_BASE}/medicaments?${searchParams}`);
    const text = await response.text();

    console.log('Search response status:', response.status);
    console.log('Search response text length:', text.length);
    console.log('Search response text (first 500 chars):', text.substring(0, 500));

    if (!response.ok) {
      const errorMsg = parseApiError(text, endpoint, response.status);
      console.error(errorMsg);
      throw new Error(errorMsg);
    }

    if (!text) {
      console.warn('Empty response from search');
      return [];
    }

    // Check if response looks like HTML/error
    if (text.trim().startsWith('<')) {
      const errorMsg = `Server returned HTML instead of JSON. Check that the backend API is running. Response: ${text.substring(0, 200)}`;
      console.error(errorMsg);
      throw new Error(errorMsg);
    }

    try {
      const data = JSON.parse(text);
      console.log('Search successful, results count:', Array.isArray(data) ? data.length : 'not an array');
      return Array.isArray(data) ? data : [];
    } catch (parseError) {
      const errorMsg = `Invalid JSON response: ${text.substring(0, 100)}`;
      console.error(errorMsg, parseError);
      throw new Error(errorMsg);
    }
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : String(e);
    console.error('Search error:', errorMessage);
    throw e;
  }
}

// Get medicament detail
export async function getMedicamentDetail(cis: string): Promise<MedicamentDetail> {
  try {
    const response = await fetch(`${API_BASE}/medicaments/${cis}`);
    const text = await response.text();

    console.log('Detail response status:', response.status);
    console.log('Detail response text:', text.substring(0, 200));

    if (!response.ok) {
      throw new Error(text || `HTTP ${response.status}: Failed to fetch medicament detail`);
    }

    if (!text) {
      throw new Error('Empty response from server');
    }

    try {
      return JSON.parse(text);
    } catch (parseError) {
      console.error('JSON parse error. Response was:', text);
      throw new Error(`Invalid JSON response: ${text.substring(0, 100)}`);
    }
  } catch (e) {
    console.error('Detail error:', e);
    throw e;
  }
}

// Import BDPM data
export async function importBDPM(sourceDir: string): Promise<ImportStatus> {
  const params = new URLSearchParams({ sourceDir });
  try {
    const response = await fetch(`${API_BASE}/imports/bdpm?${params}`, {
      method: 'POST'
    });

    const text = await response.text();

    if (!response.ok) {
      throw new Error(text || 'Import failed');
    }

    if (!text) {
      throw new Error('Empty response from import');
    }

    return JSON.parse(text);
  } catch (e) {
    if (e instanceof SyntaxError) {
      console.error('Failed to parse import response:', e);
      throw new Error('Invalid response format from server during import');
    }
    throw e;
  }
}

// Create Account
export async function signUp(params: SignUpParams): Promise<AuthResponse> {
  try {
    const response = await fetch(`${API_BASE}/auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(params)
    });

    const text = await response.text();

    if (!response.ok) {
      const errorMsg = parseApiError(text, '/api/auth/signup', response.status);
      throw new Error(errorMsg);
    }

    if (!text) {
      throw new Error('Empty response from signup');
    }

    try {
      const data = JSON.parse(text) as AuthResponse;
      return data;
    } catch (parseError) {
      console.error('JSON parse error in signup. Response was:', text);
      throw new Error(`Invalid JSON response: ${text.substring(0, 100)}`);
    }
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : String(e);
    console.error('Signup error:', errorMessage);
    throw e;
  }
}

// Login
export async function login(params: LoginParams): Promise<AuthResponse> {
  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(params)
    });

    const text = await response.text();

    if (!response.ok) {
      const errorMsg = parseApiError(text, '/api/auth/login', response.status);
      throw new Error(errorMsg);
    }

    if (!text) {
      throw new Error('Empty response from login');
    }

    try {
      const data = JSON.parse(text) as AuthResponse;
      return data;
    } catch (parseError) {
      console.error('JSON parse error in login. Response was:', text);
      throw new Error(`Invalid JSON response: ${text.substring(0, 100)}`);
    }
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : String(e);
    console.error('Login error:', errorMessage);
    throw e;
  }
}

// Logout
export async function logout(): Promise<{ success: boolean; message: string }> {
  try {
    const token = localStorage.getItem('authToken');
    const response = await fetch(`${API_BASE}/auth/logout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
      }
    });

    const text = await response.text();

    if (!response.ok) {
      console.error('Logout error:', text);
    }

    // Clear local storage regardless of response
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');

    return { success: true, message: 'Logout successful' };
  } catch (e) {
    console.error('Logout error:', e);
    // Still clear local storage on error
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    return { success: true, message: 'Logout successful' };
  }
}