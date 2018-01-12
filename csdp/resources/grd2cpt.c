/*--------------------------------------------------------------------
 *	$Id: grd2cpt.c,v 1.14 2004/07/19 02:36:13 pwessel Exp $
 *
 *	Copyright (c) 1991-2004 by P. Wessel and W. H. F. Smith
 *	See COPYING file for copying and redistribution conditions.
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; version 2 of the License.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	Contact info: gmt.soest.hawaii.edu
 *--------------------------------------------------------------------*/
/*
 * grd2cpt reads a 2d binary gridded grdfile and creates a continuous-color-
 * palette cpt file, with a non-linear histogram-equalized mapping between
 * hue and data value.  (The linear mapping can be made with makecpt.)
 *
 * Creates a cumulative distribution function f(z) describing the data
 * in the grdfile.  f(z) is sampled at z values supplied by the user
 * [with -S option] or guessed from the sample mean and standard deviation.
 * f(z) is then found by looping over the grd array for each z and counting
 * data values <= z.  Once f(z) is found then a master cpt table is resampled
 * based on a normalized f(z).
 *
 * Author:	Walter H. F. Smith
 * Date:	12-JAN-1994
 * Revised:	PW: 12-MAY-1998, for GMT 3.1
 *		PW: 08-MAR-1998, for GMT 3.2 to allow use of master cptfiles
 *		PW: 08-JUL-2000, for GMT 3.3.5
 *		JL: 27-APR-2003, added a -R option
 *		SE: 17-SEP-2003, added a -E option
 * Version:	4
 * 
 */

#include "gmt.h"

struct CDF_CPT {
	double	z;	/* Data value  */
	double	f;	/* Cumulative distribution function f(z)  */
} *cdf_cpt = NULL;

main (int argc, char **argv)
{

	int i, j, nxy, nx, ny, one_or_zero, nfound, ngood, ncdf, log_mode = 0;
	
	BOOLEAN error = FALSE, set_limits = FALSE, set_z_vals = FALSE, reverse = FALSE, ok = FALSE, continuous = FALSE;
	BOOLEAN global = FALSE, equal_inc = FALSE, no_BFN = FALSE;
	
	double *z, min_limit, max_limit, z_start, z_stop, z_inc, mean, sd;
	double w, e, s, n;
	
	//CPT_lis is list of available color tables
	char *table = CNULL, *grdfile = CNULL, CPT_lis[BUFSIZ], CPT_file[BUFSIZ], format[BUFSIZ];
	
	struct GRD_HEADER grd;

	float	*zdata;

	FILE	*fpc = NULL;
		
	argc = GMT_begin (argc, argv); //gets history (info from last run) and input params. 

	w = e = s = n = 0.0;

	/* Get list of available color tables in $GMTHOME/share/cpt */

	sprintf (CPT_lis, "%s%cshare%cGMT_CPT.lis", GMTHOME, DIR_DELIM, DIR_DELIM);

	if ((fpc = fopen (CPT_lis, "r")) == NULL) {
		fprintf (stderr, "%s: ERROR: Cannot open file %s\n", GMT_program, CPT_lis);
		exit (EXIT_FAILURE);
	}

	for (i = 1; !error && i < argc; i++) {
		if (argv[i][0] == '-') {
			switch (argv[i][1]) {
			
				/* Common parameters */
			
				case 'V':
				case 'R':
				case '\0':
					error += GMT_get_common_args (argv[i], &w, &e, &s, &n);
					break;

				/* Supplemental parameters */
				
				case 'C':	/* Get cpt table */
					table = &argv[i][2];
					break;

				case 'E':
					if (sscanf(&argv[i][2], "%d", &ncdf) != 1) {
						fprintf(stderr,"%s: GMT SYNTAX ERROR -E option:  Cannot decode value\n", GMT_program);
						error++;
					}
					if (!error) equal_inc = TRUE;
					break;

				case 'I':
					reverse = TRUE;
					break;

				case 'L':
					if (sscanf(&argv[i][2], "%lf/%lf", &min_limit, &max_limit) != 2) {
						fprintf(stderr,"%s: GMT SYNTAX ERROR -L option:  Cannot decode limits\n", GMT_program);
						error++;
					}
					else {
						if (min_limit >= max_limit) {
							fprintf(stderr,"%s: GMT SYNTAX ERROR -L option:  min_limit must be less than max_limit.\n", GMT_program);
							error++;
						}
					}
					if (!error) set_limits = TRUE;
					break;

				case 'N':
					no_BFN = TRUE;
					break;
					
				case 'Q':
					if (argv[i][2] == 'o')	/* Input data is z, but take log10(z) before interpolation colors */
						log_mode = 2;
					else			/* Input is log10(z) */
						log_mode = 1;
					break;

				case 'S':
					if (sscanf(&argv[i][2], "%lf/%lf/%lf", &z_start, &z_stop, &z_inc) != 3) {
						fprintf(stderr,"%s: GMT SYNTAX ERROR -S option:  Cannot decode values\n", GMT_program);
						error++;
					}
					else {
						if (z_stop <= z_start || z_inc <= 0.0) {
							fprintf(stderr,"%s: GMT SYNTAX ERROR -S option:  Bad arguments\n", GMT_program);
							error++;
						}
					}
					if (!error) set_z_vals = TRUE;
					break;
				case 'Z':
					continuous = TRUE;
					break;

				default:
					error = TRUE;
					GMT_default_error (argv[i][1]);
					break;
			}
		}
		else {
			grdfile = argv[i];
			if (GMT_read_grd_info (grdfile, &grd)) {
				fprintf (stderr, "%s: Error opening file %s\n", GMT_program, grdfile);
				error++;
			}
		}
	}
	
	if (argc == 1 || GMT_quick) {
		fprintf (stderr, "grd2cpt %s - Make a a linear or histogram-equalized color palette table from a grdfile\n\n", GMT_VERSION);
		fprintf (stderr, "usage: grd2cpt <grdfile> [-C<table>] [-E<nlevels>] [-I] [-L<min_limit>/<max_limit>]\n");
		fprintf (stderr, "\t[-N] [-Q[i|o]] [-Rw/e/s/n] [-S<z_start>/<z_stop>/<z_inc>] [-V] [-Z]\n");
		
		if (GMT_quick) exit (EXIT_FAILURE);
		
		fprintf (stderr, "\t<grdfile> is the name of the 2-D binary data set\n");
		fprintf (stderr, "\n\tOPTIONS:\n");
		fprintf (stderr, "\t-C  Specify a colortable [Default is rainbow]:\n");
		fprintf (stderr, "\t   [Original z-range is given in brackets]\n");
		fprintf (stderr, "\t   ---------------------------------\n");
		while (fgets (format, BUFSIZ, fpc)) if (!(format[0] == '#' || format[0] == 0)) fprintf (stderr, "\t   %s", format);
		fclose (fpc);
		fprintf (stderr, "\t   ---------------------------------\n");
		fprintf (stderr, "\t-E nlevels equidistant color levels\n");
		fprintf (stderr, "\t-I reverses the sense of the color table\n");
		fprintf (stderr, "\t-L Limit the range of the data [Default uses actual min,max of data].\n");
		fprintf (stderr, "\t-N Do Not write back-, fore-, and nan colors [Default will].\n");
		fprintf (stderr, "\t-Q assign a logarithmic colortable [Default is linear]\n");
		fprintf (stderr, "\t   -Qi: z-values are actually log10(z). Assign colors and write z. [Default]\n");
		fprintf (stderr, "\t   -Qo: z-values are z, but take log10(z), assign colors and write z.\n");
		GMT_explain_option ('R');
		fprintf (stderr, "\t-S Sample points should Step from z_start to z_stop by z_inc [Default guesses some values].\n");
		GMT_explain_option ('V');
		fprintf (stderr, "\t-Z will create a continuous color palette.\n");
		fprintf (stderr, "\t   [Default is discontinuous, i.e., constant color intervals]\n");
		exit (EXIT_FAILURE);
	}
	
	fclose (fpc);

	/* Open the specified master color table */

	/* First try current directory */

	if (table) {
		if (strstr (table, ".cpt"))
			strcpy (CPT_file, table);
		else
			sprintf (CPT_file, "%s.cpt", table);

		ok = !access (CPT_file, R_OK);
		if (ok && gmtdefs.verbose) fprintf (stderr, "%s: Reading %s in current directory\n", GMT_program, CPT_file);
	}

	if (!ok)	{	/* No table in current dir, try /share */
		if (table)
			sprintf (CPT_file, "%s%cshare%ccpt%cGMT_%s.cpt", GMTHOME, DIR_DELIM, DIR_DELIM, DIR_DELIM, table);
		else	/* Default to rainbow colors */
			sprintf (CPT_file, "%s%cshare%ccpt%cGMT_rainbow.cpt", GMTHOME, DIR_DELIM, DIR_DELIM, DIR_DELIM);

		if (access (CPT_file, R_OK)) {
			fprintf (stderr, "%s: ERROR: Cannot find colortable %s\n", GMT_program, CPT_file);
			error++;
		}
		else if (gmtdefs.verbose)
			fprintf (stderr, "%s: Reading %s\n", GMT_program, CPT_file);
	}

	if (error) exit (EXIT_FAILURE);

	GMT_put_history (argc, argv);	/* Update .gmtcommands4 */

	GMT_read_cpt (CPT_file);


	if (e > w && n > s) {
		global = (fabs (grd.x_max - grd.x_min) == 360.0);
		if (!global && (w < grd.x_min || e > grd.x_max)) error = TRUE;
		if (s < grd.y_min || n > grd.y_max) error = TRUE;
		if (error) {
			fprintf (stderr, "%s: GMT ERROR: Subset exceeds data domain!\n", GMT_program);
			exit (EXIT_FAILURE);
		}
		one_or_zero = (grd.node_offset) ? 0 : 1;
		nx = irint ((e - w) / grd.x_inc) + one_or_zero;
		ny = irint ((n - s) / grd.y_inc) + one_or_zero;
		nxy = nx * ny;
		
		zdata = (float *) GMT_memory (VNULL, (size_t) nxy, sizeof (float), GMT_program);
		
		if (GMT_read_grd (grdfile, &grd, zdata, w, e, s, n, GMT_pad, FALSE)) {
			fprintf (stderr, "%s: Error reading file %s\n", GMT_program, grdfile);
			GMT_free ((void *)zdata);
			exit (EXIT_FAILURE);
		}
	}
	else {
		nxy = grd.nx * grd.ny;
		zdata = (float *) GMT_memory (VNULL, (size_t) nxy, sizeof (float), GMT_program);

		if (GMT_read_grd (grdfile, &grd, zdata, 0.0, 0.0, 0.0, 0.0, GMT_pad, FALSE)) {
			fprintf (stderr, "%s: Error reading file %s\n", GMT_program, grdfile);
			GMT_free ((void *)zdata);
			exit (EXIT_FAILURE);
		}
	}

	/* Loop over the file and find NaNs.  If set limits, may create more NaNs  */
	nfound = 0;
	mean = 0.0;
	sd = 0.0;
	if (set_limits) {
		/* Loop over the grdfile, and set anything outside the limiting values to NaN.  */

		grd.z_min = min_limit;
		grd.z_max = max_limit;
		for (i = 0; i < nxy; i++) {
			if (GMT_is_fnan (zdata[i]))
				nfound++;
			else {
				if (zdata[i] < min_limit || zdata[i] > max_limit) {
					nfound++;
					zdata[i] = GMT_f_NaN;
				}
				else {
					mean += zdata[i];
					sd += zdata[i] * zdata[i];
				}
			}
		}
	}
	else {
		min_limit = grd.z_max;	/* This is just to double check grd.z_min, grd.z_max  */
		max_limit = grd.z_min;
		for (i = 0; i < nxy; i++) {
			if (GMT_is_fnan (zdata[i]))
				nfound++;
			else {
				if (zdata[i] < min_limit) min_limit = zdata[i];
				if (zdata[i] > max_limit) max_limit = zdata[i];
				mean += zdata[i];
				sd += zdata[i] * zdata[i];
			}
		}
		grd.z_min = min_limit;
		grd.z_max = max_limit;
	}
	ngood = nxy - nfound;	/* This is the number of non-NaN points for the cdf function  */
	mean /= ngood;
	sd /= ngood;
	sd = sqrt(sd - mean*mean);
	if (gmtdefs.verbose) {
		sprintf(format,"%%s:  Mean and S.D. of data are %s %s\n", gmtdefs.d_format, gmtdefs.d_format);
		fprintf(stderr, format, GMT_program, mean, sd);
	}

	/* Now the zdata are ready.  Decide how to make steps in z.  */
	if (set_z_vals) {
		ncdf =  (grd.z_min < z_start) ? 1 : 0;
		ncdf += (int)floor((z_stop - z_start)/z_inc) + 1;
		if (grd.z_max > z_stop) ncdf++;
		cdf_cpt = (struct CDF_CPT *)GMT_memory (VNULL, (size_t)ncdf, sizeof(struct CDF_CPT), GMT_program);
		if (grd.z_min < z_start) {
			cdf_cpt[0].z = grd.z_min;
			cdf_cpt[1].z = z_start;
			i = 2;
		}
		else {
			cdf_cpt[0].z = z_start;
			i = 1;
		}
		j = (grd.z_max > z_stop) ? ncdf - 1 : ncdf;
		while (i < j) {
			cdf_cpt[i].z = cdf_cpt[i-1].z + z_inc;
			i++;
		}
		if (j == ncdf-1) cdf_cpt[j].z = grd.z_max;
	}
	else {
		/* Make a equaldistant color map from grd.z_min to grd.z_max */
		if(equal_inc) {
			z_inc=(grd.z_max-grd.z_min)/(double)(ncdf-1);
			cdf_cpt = (struct CDF_CPT *)GMT_memory (VNULL, (size_t)ncdf, sizeof(struct CDF_CPT), GMT_program);
			for(i=0;i<ncdf;i++) {
				cdf_cpt[i].z = grd.z_min+i*z_inc;
			}
		}
		else {
			/* This is completely ad-hoc.  It chooses z based on steps
				of 0.1 for a Gaussian CDF:  */
			ncdf = 11;
			cdf_cpt = (struct CDF_CPT *)GMT_memory (VNULL, (size_t)ncdf, sizeof(struct CDF_CPT), GMT_program);
                	/* Stupid bug fix here:  
                	        If (mean-1.28155*sd <= grd.z_min || mean+1.28155*sd >= grd.z_max) then
                	          reset mean and sd so they fit inside available range:  */
                          
			if ((mean - 1.28155*sd) <= grd.z_min || (mean + 1.28155*sd) >= grd.z_max) {
				mean = 0.5 * (grd.z_min + grd.z_max);
				sd = (grd.z_max - mean) / 1.5;
				if (sd <= 0.0) {
					fprintf (stderr, "%s:  ERROR.  Min and Max data values are equal.\n", GMT_program);
					exit (EXIT_FAILURE);
				}
			}	/* End of stupid bug fix  */
		
			cdf_cpt[0].z = grd.z_min;
			cdf_cpt[1].z = mean - 1.28155 * sd;
			cdf_cpt[2].z = mean - 0.84162 * sd;
			cdf_cpt[3].z = mean - 0.52440 * sd;
			cdf_cpt[4].z = mean - 0.25335 * sd;
			cdf_cpt[5].z = mean;
			cdf_cpt[6].z = mean + 0.25335 * sd;
			cdf_cpt[7].z = mean + 0.52440 * sd;
			cdf_cpt[8].z = mean + 0.84162 * sd;
			cdf_cpt[9].z = mean + 1.28155 * sd;
			cdf_cpt[10].z = grd.z_max;
		}
	}
	
	/* Get here when we are ready to go.  cdf_cpt[].z contains the sample points.  */

	if (gmtdefs.verbose) sprintf (format, "%%s: z = %s and CDF(z) = %s\n", gmtdefs.d_format, gmtdefs.d_format);
	for (j = 0; j < ncdf; j++) {
		if (cdf_cpt[j].z <= grd.z_min)
			cdf_cpt[j].f = 0.0;
		else if (cdf_cpt[j].z >= grd.z_max)
			cdf_cpt[j].f = 1.0;
		else {
			nfound = 0;
			for (i = 0; i < nxy; i++) {
				if (!GMT_is_fnan (zdata[i]) && zdata[i] <= cdf_cpt[j].z) nfound++;
			}
			cdf_cpt[j].f = (double)(nfound-1)/(double)(ngood-1);
		}
		if (gmtdefs.verbose) fprintf (stderr, format, GMT_program, cdf_cpt[j].z, cdf_cpt[j].f);
	}

	/* Now the cdf function has been found.  We now resample the chosen cptfile  */

	/* Write to GMT_stdout.  */

	fprintf (GMT_stdout, "#\tcpt file created by: %s", GMT_program);
	for (i = 1; i < argc; i++) fprintf (GMT_stdout, " %s", argv[i]);
	fprintf (GMT_stdout, "\n");

	
	z = (double *) GMT_memory (VNULL, (size_t)ncdf, sizeof (double), GMT_program);
	for (i = 0; i < ncdf; i++) z[i] = cdf_cpt[i].z;
	if (log_mode == 2) for (i = 0; i < ncdf; i++) z[i] = d_log10 (z[i]);	/* Make log10(z) values for interpolation step */

	GMT_sample_cpt (z, -ncdf, continuous, reverse, log_mode, no_BFN);	/* -ve to keep original colors */

	GMT_free ((void *)cdf_cpt);
	GMT_free ((void *)zdata);
	GMT_free ((void *)z);
	
	GMT_end (argc, argv);
}
