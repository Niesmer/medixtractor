import { useState } from "react";
import { useNavigate } from "react-router";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Alert, AlertDescription } from "./ui/alert";
import { AlertCircle, CheckCircle2, Loader } from "lucide-react";
import { signUp } from "../services/api";

export function SignupPage() {
  const navigate = useNavigate();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [role, setRole] = useState("ADMIN");
  const [siretSiren, setSiretSiren] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    // Validation
    if (!fullName.trim() || !email.trim() || !password || !confirmPassword) {
      setError("Veuillez remplir tous les champs.");
      return;
    }

    if (password.length < 6) {
      setError("Le mot de passe doit contenir au moins 6 caractères.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Les mots de passe ne correspondent pas.");
      return;
    }

    if ((role === "DOCTOR" || role === "PHARMACIST") && !siretSiren.trim()) {
      setError(`${role} doit fournir un numéro SIRET/SIREN.`);
      return;
    }

    setLoading(true);

    try {
      const response = await signUp({
        fullName: fullName.trim(),
        email: email.trim().toLowerCase(),
        password,
        role,
        siretSiren: siretSiren.trim() || undefined
      });

      if (response.success) {
        setSuccess(true);
        // Store token
        localStorage.setItem("authToken", response.token);
        localStorage.setItem("user", JSON.stringify(response.user));

        // Redirect to home after 2 seconds
        setTimeout(() => {
          navigate("/");
        }, 2000);
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Signup failed";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-8rem)] bg-gradient-to-b from-blue-50 to-white flex items-center justify-center px-4 py-12">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Créer un compte</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {success && (
            <Alert className="border-green-200 bg-green-50">
              <CheckCircle2 className="h-4 w-4 text-green-600" />
              <AlertDescription className="text-green-800">
                Compte créé avec succès ! Redirection...
              </AlertDescription>
            </Alert>
          )}

          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <form onSubmit={handleSignup} className="space-y-4">
            {/* Full Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nom complet
              </label>
              <Input
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="Jean Dupont"
                disabled={loading || success}
              />
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                E-mail
              </label>
              <Input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="jean@example.com"
                disabled={loading || success}
              />
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mot de passe
              </label>
              <Input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Au moins 6 caractères"
                disabled={loading || success}
              />
            </div>

            {/* Confirm Password */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Confirmer le mot de passe
              </label>
              <Input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Confirmer le mot de passe"
                disabled={loading || success}
              />
            </div>

            {/* Role */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Rôle
              </label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                disabled={loading || success}
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="ADMIN">Administrateur</option>
                <option value="DOCTOR">Médecin</option>
                <option value="PHARMACIST">Pharmacien</option>
              </select>
            </div>

            {/* SIRET/SIREN (conditional) */}
            {(role === "DOCTOR" || role === "PHARMACIST") && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {role === "DOCTOR" ? "SIREN" : "SIRET"} (Numéro d'inscription professionnel)
                </label>
                <Input
                  type="text"
                  value={siretSiren}
                  onChange={(e) => setSiretSiren(e.target.value.replace(/\D/g, ""))}
                  placeholder="14 chiffres"
                  maxLength={14}
                  disabled={loading || success}
                />
                <p className="text-xs text-gray-500 mt-1">
                  {role === "DOCTOR"
                    ? "Entrez votre numéro SIREN (14 chiffres)"
                    : "Entrez votre numéro SIRET (14 chiffres)"}
                </p>
              </div>
            )}

            {/* Submit Button */}
            <Button
              type="submit"
              disabled={loading || success}
              className="w-full"
            >
              {loading ? (
                <>
                  <Loader className="w-4 h-4 mr-2 animate-spin" />
                  Création de compte...
                </>
              ) : (
                "Créer un compte"
              )}
            </Button>
          </form>

          {/* Login Link */}
          <p className="text-sm text-gray-600 text-center">
            Vous avez déjà un compte ?{" "}
            <a
              href="/login"
              className="text-blue-600 hover:text-blue-700 font-medium"
            >
              Se connecter ici
            </a>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
